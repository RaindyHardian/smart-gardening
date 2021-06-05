package com.pistachio.smartgardening.data.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DiseaseEntity(
    @field:SerializedName("id")
    var id: String = "",

    @field:SerializedName("name")
    var name: String = "",

    @field:SerializedName("latinName")
    var latinName: String = "",

    @field:SerializedName("description")
    var description: String = "",

    @field:SerializedName("type")
    var type: String = "",

    @field:SerializedName("habitat")
    var habitat: String = "",

    @field:SerializedName("resolve")
    var resolve: String = ""
): Parcelable