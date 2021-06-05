package com.pistachio.smartgardening.ui.history

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.pistachio.smartgardening.data.entity.PlantEntity
import com.pistachio.smartgardening.data.source.local.repository.PlantRepository

class HistoryViewModel(application: Application) : ViewModel() {

    private val plantRepository: PlantRepository = PlantRepository(application)

    fun getAllPlants(): LiveData<List<PlantEntity>> = plantRepository.getAllPlants()

    fun clearAll(){
        plantRepository.clearAll()
    }

}