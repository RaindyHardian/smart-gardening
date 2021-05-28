package com.pistachio.smartgardening.ui.detail

import android.app.Application
import androidx.lifecycle.ViewModel
import com.pistachio.smartgardening.data.PlantEntity
import com.pistachio.smartgardening.data.source.local.repository.PlantRepository

class DetailViewModel (application: Application) : ViewModel(){

    private val plantRepository: PlantRepository = PlantRepository(application)

    fun insert(plant: PlantEntity) {
        plantRepository.insert(plant)
    }
}