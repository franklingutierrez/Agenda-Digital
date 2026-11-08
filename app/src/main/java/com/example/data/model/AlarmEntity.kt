package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val hour: Int, // 0 - 23
    val minute: Int, // 0 - 59
    val label: String = "Alarma",
    val isEnabled: Boolean = true,
    val repeatDays: String = "1,2,3,4,5", // 1=Dom, 2=Lun, 3=Mar, 4=Mie, 5=Jue, 6=Vie, 7=Sab
    val vibrate: Boolean = true,
    val soundName: String = "Tono Predeterminado",
    val soundUri: String = "" // Uri string para tonos del sistema o archivos MP3 personalizados
)
