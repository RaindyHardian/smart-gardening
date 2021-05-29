package com.pistachio.smartgardening.utils

import com.pistachio.smartgardening.data.PlantEntity
import com.pistachio.smartgardening.data.source.remote.response.PlantResponse

object DataMapper {
    fun mapResponsesToEntities(input: List<PlantResponse>): PlantEntity {
        input.map {
            return PlantEntity(
                dbId = 0,
                plantId = it.id!!,
                name = it.name!!,
                latinName = it.latinName!!,
                description = it.description!!,
                habitat = it.habitat!!,
                bestPlacing = it.bestPlacing!!,
                disease = it.disease!!,
                marketPrice = it.marketPrice!!,
                imagePath = it.picture!!
            )
        }
        return PlantEntity()
    }
}