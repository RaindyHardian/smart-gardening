package com.pistachio.smartgardening.ui.detail

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage
import com.pistachio.smartgardening.databinding.ActivityDetailBinding
import com.pistachio.smartgardening.databinding.PlantDetailBinding
import com.pistachio.smartgardening.ui.data.PlantEntity
import java.io.File
import java.io.IOException


class DetailActivity : AppCompatActivity() {
    companion object{
        const val EXTRA_PLANT = "extra_plant"
    }
    private lateinit var binding: ActivityDetailBinding
    private lateinit var detailBinding: PlantDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        detailBinding = binding.detailContent

        setContentView(binding.root)

        val plant = intent.getParcelableExtra<PlantEntity>(EXTRA_PLANT) as PlantEntity

        loadPlant(plant)

        detailBinding.cardView.setOnClickListener {
            Toast.makeText(this,plant.imagePath,Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadPlant(plantEntity: PlantEntity){
        detailBinding.txtPlantName.text = plantEntity.name
        detailBinding.txtPlantLatinName.text = plantEntity.latinName
        detailBinding.txtDescription.text = plantEntity.description
        detailBinding.txtHabitat.text = plantEntity.habitat
        detailBinding.txtBestPlacing.text = plantEntity.bestPlacing
        detailBinding.txtMarketPrice.text = plantEntity.marketPrice

        var diseaseString = ""
        var diseaseCount = plantEntity.disease.size
        for (i in 0..diseaseCount - 1){
            if(i == 0){
                diseaseString += plantEntity.disease.get(i)
            }else{
                diseaseString += ", ${plantEntity.disease.get(i)}"
            }
        }

        detailBinding.txtDisease.text = diseaseString

        val imgFile = File(plantEntity.imagePath)

        if (imgFile.exists()) {
            Glide.with(this).load(imgFile).into(detailBinding.imgPlant)
        }
    }
}