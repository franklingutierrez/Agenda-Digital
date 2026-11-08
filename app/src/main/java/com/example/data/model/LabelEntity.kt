package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "labels")
data class LabelEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val colorHex: String, // e.g. "#93C5FD", "#86EFAC", "#FDBA74", etc.
    val iconName: String = "Bookmark"
)
