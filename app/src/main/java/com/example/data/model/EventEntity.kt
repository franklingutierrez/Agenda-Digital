package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val labelId: Long? = null,
    val startMillis: Long,
    val endMillis: Long,
    val location: String = "",
    val isAllDay: Boolean = false,
    val isPeruHoliday: Boolean = false,
    val externalCalendarEventId: String? = null // For Google Calendar sync reference
)
