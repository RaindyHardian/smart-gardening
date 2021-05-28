package com.pistachio.smartgardening.ui.camera

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.View
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.pistachio.smartgardening.R
import com.pistachio.smartgardening.data.source.remote.network.ApiConfig
import com.pistachio.smartgardening.data.source.remote.response.ListPlantResponse
import com.pistachio.smartgardening.databinding.ActivityCameraBinding
import com.pistachio.smartgardening.data.PlantEntity
import com.pistachio.smartgardening.ui.detail.DetailActivity
import com.pistachio.smartgardening.utils.DummyData
import kotlinx.android.synthetic.main.activity_camera.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.TensorOperator
import org.tensorflow.lite.support.common.TensorProcessor
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp
import org.tensorflow.lite.support.label.TensorLabel
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding
    private lateinit var dummyPlants: List<PlantEntity>
    private var imageCapture: ImageCapture? = null

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    protected var tflite: Interpreter? = null
    private var inputImageBuffer: TensorImage? = null
    private var imageSizeX = 0
    private var imageSizeY = 0
    private var outputProbabilityBuffer: TensorBuffer? = null
    private var probabilityProcessor: TensorProcessor? = null
    private val IMAGE_MEAN = 0.0f
    private val IMAGE_STD = 1.0f
    private val PROBABILITY_MEAN = 0.0f
    private val PROBABILITY_STD = 255.0f
    private lateinit var bitmap: Bitmap
    private var labels: List<String>? = null

    private lateinit var imagePath: String
    private lateinit var imageFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Capture Plant"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
        dummyPlants = DummyData.generateDummyPlants()

        binding.cameraCaptureButton.setOnClickListener { takePhoto() }
        binding.viewFinder.scaleType = PreviewView.ScaleType.FIT_CENTER

        outputDirectory = getOutputDirectory()

        cameraExecutor = Executors.newSingleThreadExecutor()

//        try {
//            tflite = Interpreter(loadmodelfile(this))
//        } catch (e: java.lang.Exception) {
//            e.printStackTrace()
//        }

        binding.btnRecapture.setOnClickListener {
            img_saved.visibility = View.GONE
            viewFinder.visibility = View.VISIBLE

            binding.buttonConfirmation.visibility = View.GONE
            camera_capture_button.visibility = View.VISIBLE

            cameraExecutor = Executors.newSingleThreadExecutor()
        }

        binding.btnConfirm.setOnClickListener {
            // SEND API
            var requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), imageFile)
            val body: MultipartBody.Part =
                MultipartBody.Part.createFormData("image", imageFile.name, requestFile)
            val client = ApiConfig.getApiService().postImage(body)
            client.enqueue(object : Callback<ListPlantResponse> {
                override fun onResponse(
                    call: Call<ListPlantResponse>,
                    response: Response<ListPlantResponse>
                ) {
                    if (response.isSuccessful) {
                        Log.d("API SUCCESS","Photo Uploaded")
                        //Dummy Plant
                        var random = (dummyPlants.indices).random()

                        dummyPlants[random].imagePath = imagePath

                        // if success then send the response to detail -> use response.body()?.plant or convert into Entity
                        val moveDetail = Intent(this@CameraActivity, DetailActivity::class.java)
                        moveDetail.putExtra(DetailActivity.EXTRA_PLANT, dummyPlants[random])
                        moveDetail.putExtra(DetailActivity.EXTRA_STATUS,200)
                        startActivity(moveDetail)

                        /*Toast.makeText(
                            this@CameraActivity,
                            response.body()?.plant.toString(),
                            Toast.LENGTH_LONG
                        ).show()*/
                    } else {
                        Log.e(
                            "API NOT SUCESS",
                            "UPLOADED BUT ERROR: ${response.message()} & ${response.body()?.error.toString()}"
                        )
                        Toast.makeText(
                            this@CameraActivity,
                            "UPLOADED BUT ERROR : ${response.body()?.error.toString()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ListPlantResponse>, t: Throwable) {
                    Log.e("API FAIL", "onFailure: ${t.message.toString()}")
                    Toast.makeText(
                        this@CameraActivity,
                        "ON FAILURE ERROR: ${t.message.toString()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        }
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time-stamped output file to hold the image
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(
                FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg"
        )

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo capture succeeded: $savedUri"
                    //Toast.makeText(baseContext, photoFile.absolutePath, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)

                    var oriBitmap: Bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)

                    bitmap = rotateImageIfRequired(oriBitmap, savedUri)
                    binding.imgSaved.setImageBitmap(bitmap)

                    binding.imgSaved.visibility = View.VISIBLE
                    binding.viewFinder.visibility = View.GONE

                    binding.buttonConfirmation.visibility = View.VISIBLE
                    binding.cameraCaptureButton.visibility = View.GONE

                    imagePath = photoFile.absolutePath
                    imageFile = photoFile

                    cameraExecutor.shutdown()
                }
            })
    }

    @SuppressLint("RestrictedApi")
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()


            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .setTargetResolution(Size(480, 640))
                .build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }


    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun getCameraPhotoOrientation(imagePath: String): Int {
        var rotate = 0
        try {
            val imageFile = File(imagePath)
            val exif = ExifInterface(imageFile.absolutePath)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270
                ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180
                ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90
            }
            Log.i("RotateImage", "Exif orientation: $orientation")
            Log.i("RotateImage", "Rotate value: $rotate")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return rotate
    }

    @Throws(IOException::class)
    private fun rotateImageIfRequired(img: Bitmap, selectedImage: Uri): Bitmap {
        val ei = ExifInterface(selectedImage.path!!)
        val orientation =
            ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(img, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(img, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(img, 270)
            else -> img
        }
    }

    private fun rotateImage(img: Bitmap, degree: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        val rotatedImg = Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
        img.recycle()
        return rotatedImg
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    // tensor
    private fun loadImage(bitmap: Bitmap): TensorImage? {
        // Loads bitmap into a TensorImage.
        inputImageBuffer!!.load(bitmap)

        // Creates processor for the TensorImage.
        val cropSize = Math.min(bitmap.width, bitmap.height)
        // TODO(b/143564309): Fuse ops inside ImageProcessor.
        val imageProcessor: ImageProcessor = ImageProcessor.Builder()
            .add(ResizeWithCropOrPadOp(cropSize, cropSize))
            .add(ResizeOp(imageSizeX, imageSizeY, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
            .add(getPreprocessNormalizeOp())
            .build()
        return imageProcessor.process(inputImageBuffer)
    }

    @Throws(IOException::class)
    private fun loadmodelfile(activity: Activity): MappedByteBuffer {
        val fileDescriptor = activity.assets.openFd("PlantModel.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startoffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startoffset, declaredLength)
    }

    private fun getPreprocessNormalizeOp(): TensorOperator {
        return NormalizeOp(IMAGE_MEAN, IMAGE_STD)
    }

    private fun getPostprocessNormalizeOp(): TensorOperator {
        return NormalizeOp(PROBABILITY_MEAN, PROBABILITY_STD)
    }

    private fun showresult(imagePath: String) {
        try {
            labels = FileUtil.loadLabels(this, "PlantModel.txt")
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        val labeledProbability =
            TensorLabel(labels!!, probabilityProcessor!!.process(outputProbabilityBuffer))
                .mapWithFloatValue
        val maxValueInMap = Collections.max(labeledProbability.values)
        for ((key, value) in labeledProbability) {
            if (value == maxValueInMap) {
                /*Toast.makeText(
                    this,
                    key,
                    Toast.LENGTH_LONG
                ).show()*/
                val plantEntity = PlantEntity(name = key, imagePath = imagePath)
                val moveDetail = Intent(this@CameraActivity, DetailActivity::class.java)
                moveDetail.putExtra(DetailActivity.EXTRA_PLANT, plantEntity)
                startActivity(moveDetail)
            }
        }
        Log.d("PROB", labeledProbability.toString())
    }
}