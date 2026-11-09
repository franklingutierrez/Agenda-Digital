package com.example.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object TaskRecurrenceHelper {

    /**
     * Calcula la próxima fecha de vencimiento según el intervalo de recurrencia.
     * Preserva la hora y minuto original de la tarea.
     */
    fun calculateNextDueDate(currentDueDateMillis: Long, recurrenceInterval: String): Long {
        val zone = ZoneId.systemDefault()
        val currentDateTime = Instant.ofEpochMilli(currentDueDateMillis)
            .atZone(zone)
            .toLocalDateTime()

        val nextDateTime = when (recurrenceInterval.uppercase()) {
            "DAILY" -> currentDateTime.plusDays(1)
            "WEEKLY" -> currentDateTime.plusWeeks(1)
            "MONTHLY" -> currentDateTime.plusMonths(1)
            "YEARLY" -> currentDateTime.plusYears(1)
            else -> currentDateTime
        }

        return nextDateTime.atZone(zone).toInstant().toEpochMilli()
    }

    /**
     * Devuelve una descripción legible de la fecha y hora de vencimiento.
     * Ej: "Hoy, 3:30 PM", "Mañana, 9:00 AM", "15 nov, 10:00 AM".
     */
    fun formatFriendlyDueDate(dueDateMillis: Long): String {
        val zone = ZoneId.systemDefault()
        val dueDateTime = Instant.ofEpochMilli(dueDateMillis).atZone(zone).toLocalDateTime()
        val today = LocalDate.now(zone)
        val dueDate = dueDateTime.toLocalDate()

        val timeFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault())
        val timeStr = dueDateTime.format(timeFormatter)

        return when (dueDate) {
            today -> "Hoy, $timeStr"
            today.plusDays(1) -> "Mañana, $timeStr"
            today.minusDays(1) -> "Ayer, $timeStr"
            else -> {
                val dateFormatter = if (dueDate.year == today.year) {
                    DateTimeFormatter.ofPattern("d 'de' MMM, ", Locale("es", "PE"))
                } else {
                    DateTimeFormatter.ofPattern("d/MM/yy, ", Locale.getDefault())
                }
                dueDate.format(dateFormatter) + timeStr
            }
        }
    }
}
