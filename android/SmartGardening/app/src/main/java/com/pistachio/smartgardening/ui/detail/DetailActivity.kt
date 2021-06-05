package com.pistachio.smartgardening.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.pistachio.smartgardening.data.entity.PlantEntity
import com.pistachio.smartgardening.databinding.ActivityDetailBinding
import com.pistachio.smartgardening.databinding.PlantDetailBinding
import com.pistachio.smartgardening.utils.ViewModelFactory
import java.io.File


class DetailActivity : AppCompatActivity() {
    companion object{
        const val EXTRA_PLANT = "extra_plant"
        const val EXTRA_STATUS = "extra_status"
    }
    private lateinit var binding: ActivityDetailBinding
    private lateinit var detailBinding: PlantDetailBinding
    private lateinit var viewModel: DetailViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        detailBinding = binding.detailContent
        val plant = intent.getParcelableExtra<PlantEntity>(EXTRA_PLANT) as PlantEntity
        val status = intent.getIntExtra(EXTRA_STATUS, 404)

        val plantBundle = Bundle()
        plantBundle.putParcelable("extra_bundle",plant)

        val sectionsPagerAdapter = SectionPagerAdapter(this, supportFragmentManager, plantBundle)
        binding.viewPager.adapter = sectionsPagerAdapter
        binding.tabs.setupWithViewPager(binding.viewPager)

        setContentView(binding.root)
        supportActionBar?.title = "Plant Details"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val factory = ViewModelFactory.getInstance(this.application)
        viewModel = ViewModelProvider(this, factory)[DetailViewModel::class.java]
        loadPlant(plant)
        if(status == 200) {
            viewModel.insert(plant)
        }
    }

    private fun loadPlant(plantEntity: PlantEntity){
        detailBinding.txtPlantName.text = plantEntity.name
        detailBinding.txtPlantLatinName.text = plantEntity.latinName

        val imgFile = File(plantEntity.imagePath)

        if (imgFile.exists()) {
            Glide.with(this).load(imgFile).into(detailBinding.imgPlant)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val status = intent.getIntExtra(EXTRA_STATUS, 404)
        return if(status == 200){
            super.onSupportNavigateUp()
        }else{
            onBackPressed()
            true
        }
    }
}