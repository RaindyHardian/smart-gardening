package com.pistachio.smartgardening.data.source.local.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.pistachio.smartgardening.data.PlantEntity

@Dao
interface PlantDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(plant: PlantEntity)

    @Query("DELETE from plantEntities")
    fun clearAll()

    @Query("SELECT * from plantEntities ORDER BY dbId DESC")
    fun getAllPlants(): LiveData<List<PlantEntity>>
}