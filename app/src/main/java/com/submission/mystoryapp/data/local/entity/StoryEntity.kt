package com.submission.mystoryapp.data.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "story")
@Parcelize
data class StoryEntity(
    @field: PrimaryKey
    @field: ColumnInfo(name = "id")
    val id: String,

    @field: ColumnInfo(name = "name")
    val name: String,

    @field: ColumnInfo(name = "description")
    val description: String,

    @field: ColumnInfo(name = "photoUrl")
    val photoUrl: String,

    @field: ColumnInfo(name = "createdAt")
    val createdAt: String,

    @field: ColumnInfo(name = "lat")
    val lat: Double,

    @field: ColumnInfo(name = "lon")
    val lon: Double,
): Parcelable