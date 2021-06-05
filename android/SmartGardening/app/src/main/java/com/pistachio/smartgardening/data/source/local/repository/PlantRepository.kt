package com.pistachio.smartgardening.data.source.local.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.pistachio.smartgardening.data.entity.PlantEntity
import com.pistachio.smartgardening.data.source.local.room.PlantDao
import com.pistachio.smartgardening.data.source.local.room.PlantRoomDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class PlantRepository(application: Application) {

    private val plantDao: PlantDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
    init {
        val db = PlantRoomDatabase.getDatabase(application)
        plantDao = db.plantDao()
    }

    fun insert(plant: PlantEntity) {
        executorService.execute { plantDao.insert(plant) }
    }
    fun clearAll() {
        executorService.execute { plantDao.clearAll() }
    }

    fun getAllPlants(): LiveData<List<PlantEntity>> = plantDao.getAllPlants()
}