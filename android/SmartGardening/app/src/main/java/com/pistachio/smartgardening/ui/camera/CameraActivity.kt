package com.pistachio.smartgardening.ui.camera

import android.Manifest
import android.annotation.SuppressLint
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
import com.pistachio.smartgardening.utils.DataMapper
import com.pistachio.smartgardening.utils.DummyData
import kotlinx.android.synthetic.main.activity_camera.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
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

    private lateinit var bitmap: Bitmap

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

        binding.btnRecapture.setOnClickListener {
            img_saved.visibility = View.GONE
            viewFinder.visibility = View.VISIBLE

            binding.buttonConfirmation.visibility = View.GONE
            camera_capture_button.visibility = View.VISIBLE

            cameraExecutor = Executors.newSingleThreadExecutor()
        }

        binding.btnConfirm.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
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
                    binding.progressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        Log.d("API SUCCESS", "Photo Uploaded")
                        //Dummy Plant
                        var random = (dummyPlants.indices).random()

                        dummyPlants[random].imagePath = imageFile.absolutePath

                        // if success then send the response to detail -> use response.body()?.plant or convert into Entity
                        // val plantData = DataMapper.mapResponsesToEntities(response.body()?.plant!!)

                        val moveDetail = Intent(this@CameraActivity, DetailActivity::class.java)
                        moveDetail.putExtra(DetailActivity.EXTRA_PLANT, dummyPlants[random])
                        moveDetail.putExtra(DetailActivity.EXTRA_STATUS, 200)
                        startActivity(moveDetail)
                        finish()
                    } else {
                        Log.e(
                            "API NOT SUCESS",
                            "UPLOADED BUT ERROR: ${response.message()} & ${response.body()?.error.toString()}"
                        )
                        Toast.makeText(
                            this@CameraActivity,
                            "Error uploading photo",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ListPlantResponse>, t: Throwable) {
                    binding.progressBar.visibility = View.GONE
                    Log.e("API FAIL", "onFailure: ${t.message.toString()}")
                    Toast.makeText(
                        this@CameraActivity,
                        "Error uploading photo",
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
                    Log.d(TAG, msg)

                    var oriBitmap: Bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)

                    bitmap = rotateImageIfRequired(oriBitmap, savedUri)
                    binding.imgSaved.setImageBitmap(bitmap)

                    binding.imgSaved.visibility = View.VISIBLE
                    binding.viewFinder.visibility = View.GONE

                    binding.buttonConfirmation.visibility = View.VISIBLE
                    binding.cameraCaptureButton.visibility = View.GONE

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
}