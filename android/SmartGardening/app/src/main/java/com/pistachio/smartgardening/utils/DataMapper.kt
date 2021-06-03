package com.pistachio.smartgardening.utils

import com.pistachio.smartgardening.data.PlantEntity
import com.pistachio.smartgardening.data.source.remote.response.PlantResponse

object DataMapper {
    fun mapResponsesToEntities(input: PlantResponse, location: String, image: String): PlantEntity {
        return PlantEntity(
            dbId = 0,
            plantId = input.id,
            name = input.name,
            latinName = input.latinName,
            description = input.description,
            habitat = input.habitat,
            bestPlacing = input.bestPlacing,
            disease = input.disease!!,
            marketPrice = input.marketPrice,
            imagePath = image,
            location = location
        )
    }
}