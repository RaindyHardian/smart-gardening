package com.pistachio.smartgardening.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class DiseaseItemResponse(
    @field:SerializedName("habitat")
    val habitat: String? = null,

    @field:SerializedName("cause")
    val cause: String? = null,

    @field:SerializedName("resolve")
    val resolve: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("latinName")
    val latinName: String? = null,

    @field:SerializedName("type")
    val type: String? = null
)

