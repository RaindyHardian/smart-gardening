package com.pistachio.smartgardening.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class ListPlantResponse(

    @field:SerializedName("plant")
    val plant: List<PlantResponse?>? = null,

    @field:SerializedName("error")
    val error: String
)

