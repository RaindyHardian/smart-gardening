package com.pistachio.smartgardening.ui.data

import android.net.Uri
import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "plantEntities")
data class PlantEntity(
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name="plantId")
    var id: Int? = null,

    @ColumnInfo(name = "plantName")
    var name: String? = null,

    @ColumnInfo(name = "plantLatinName")
    var latinName: String? = null,

    @ColumnInfo(name = "plantDescription")
    var description: String? = null,

    @ColumnInfo(name = "plantPreservation")
    var preservation: String? = null,

    @ColumnInfo(name = "plantImagePath")
    var imagePath: String? = null
): Parcelable
