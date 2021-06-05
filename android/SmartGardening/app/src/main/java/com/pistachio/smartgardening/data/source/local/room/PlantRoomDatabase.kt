package com.pistachio.smartgardening.data.source.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pistachio.smartgardening.data.entity.PlantEntity
import com.pistachio.smartgardening.utils.Converter
import com.pistachio.smartgardening.utils.DiseaseConverter

@Database(entities = [PlantEntity::class], version = 1)
@TypeConverters(value = [Converter::class, DiseaseConverter::class])
abstract class PlantRoomDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao
    companion object {
        @Volatile
        private var INSTANCE: PlantRoomDatabase? = null
        @JvmStatic
        fun getDatabase(context: Context): PlantRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PlantRoomDatabase::class.java,
                    "plant.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}