package com.pistachio.smartgardening.utils

import com.pistachio.smartgardening.data.entity.DiseaseEntity
import com.pistachio.smartgardening.data.entity.PlantEntity
import com.pistachio.smartgardening.data.source.remote.response.PlantResponse

object DataMapper {
    fun mapResponsesToEntities(input: PlantResponse, location: String, image: String, date: String): PlantEntity {
        val diseaseList = ArrayList<DiseaseEntity>()
        input.disease?.map {
            val diseaseItem = DiseaseEntity(
                id = it?.id ?: "",
                name = it?.name ?: "",
                latinName = it?.latinName ?: "-",
                description = it?.description ?: "",
                type = it?.description ?: "",
                habitat = it?.habitat ?: "-",
                cause = it?.cause ?: "-",
                resolve = it?.resolve ?: "",
            )
            diseaseList.add(diseaseItem)
        }
        return PlantEntity(
            dbId = 0,
            plantId = input.id,
            name = input.name,
            latinName = input.latinName,
            description = input.description,
            habitat = input.habitat,
            bestPlacing = input.bestPlacing,
            disease = diseaseList,
            marketPrice = input.marketPrice,
            imagePath = image,
            location = location,
            date = date
        )
    }
}