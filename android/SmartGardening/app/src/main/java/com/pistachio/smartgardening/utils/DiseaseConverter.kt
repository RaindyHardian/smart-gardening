package com.pistachio.smartgardening.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pistachio.smartgardening.data.entity.DiseaseEntity
import java.util.*

class DiseaseConverter {
    private val gson = Gson()
    @TypeConverter
    fun stringToList(data: String?): List<DiseaseEntity> {
        if (data == null) {
            return Collections.emptyList()
        }

        val listType = object : TypeToken<List<DiseaseEntity>>() {

        }.type

        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun listToString(someObjects: List<DiseaseEntity>): String {
        return gson.toJson(someObjects)
    }
}