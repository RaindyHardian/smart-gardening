package com.pistachio.smartgardening.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class PlantResponse(

    @field:SerializedName("marketPrice")
    val marketPrice: String? = null,

    @field:SerializedName("disease")
    val disease: List<String?>? = null,

    @field:SerializedName("habitat")
    val habitat: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("latinName")
    val latinName: String? = null,

    @field:SerializedName("picture")
    val picture: String? = null,

    @field:SerializedName("bestPlacing")
    val bestPlacing: String? = null
)
