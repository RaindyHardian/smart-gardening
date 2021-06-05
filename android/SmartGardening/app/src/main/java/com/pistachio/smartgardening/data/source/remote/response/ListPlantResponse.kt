package com.pistachio.smartgardening.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class ListPlantResponse(

    @field:SerializedName("plant")
    val plant: PlantResponse,

    @field:SerializedName("storage")
    val storage: String,

    @field:SerializedName("error")
    val error: String
)

