package com.pistachio.smartgardening.data.source.remote.response

import com.google.gson.annotations.SerializedName
import com.pistachio.smartgardening.data.entity.DiseaseEntity

data class PlantResponse(

    @field:SerializedName("marketPrice")
    var marketPrice: String = "",

    @field:SerializedName("disease")
    val disease: List<DiseaseItemResponse?>? = null,

    @field:SerializedName("habitat")
    var habitat: String = "",

    @field:SerializedName("name")
    var name: String = "",

    @field:SerializedName("description")
    var description: String = "",

    @field:SerializedName("id")
    var id: String = "",

    @field:SerializedName("latinName")
    var latinName: String = "",

    @field:SerializedName("bestPlacing")
    var bestPlacing: String = "",

    @field:SerializedName("tips")
    var tips: String = "",

    @field:SerializedName("watering")
    var watering: String = "",

    @field:SerializedName("fertilizer")
    var fertilizer: String = "",
)
