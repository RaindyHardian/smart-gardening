package com.pistachio.smartgardening.data.entity

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
@Entity(tableName = "plantEntities")
data class PlantEntity(

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "dbId")
    var dbId: Int = 0,

    @ColumnInfo(name= "plantId")
    var plantId: String = "",

    @ColumnInfo(name = "plantName")
    var name: String = "",

    @ColumnInfo(name = "plantLatinName")
    var latinName: String = "",

    @ColumnInfo(name = "plantDescription")
    var description: String = "",

    @ColumnInfo(name = "plantHabitat")
    var habitat: String = "",

    @ColumnInfo(name = "plantBestPlacing")
    var bestPlacing: String = "",

    @ColumnInfo(name = "plantDisease")
    var disease: @RawValue List<DiseaseEntity> = listOf(),

    @ColumnInfo(name = "plantMarketPrice")
    var marketPrice: String = "",

    @ColumnInfo(name = "tips")
    var tips: String = "",

    @ColumnInfo(name = "watering")
    var watering: String = "",

    @ColumnInfo(name = "fertilizer")
    var fertilizer: String = "",

    @ColumnInfo(name = "plantImagePath")
    var imagePath: String = "",

    @ColumnInfo(name = "plantLocation")
    var location: String = "",

    @ColumnInfo(name = "imgDate")
    var date: String
): Parcelable
