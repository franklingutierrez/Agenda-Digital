package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TaskPriority {
    LOW,
    MEDIUM,
    HIGH
}

enum class TaskRecurrence(val label: String, val icon: String) {
    NONE("No se repite", "⛔"),
    DAILY("Diaria", "🔄"),
    WEEKLY("Semanal", "📅"),
    MONTHLY("Mensual", "🗓️"),
    YEARLY("Anual", "🎂")
}

enum class EisenhowerQuadrant(val title: String, val subtitle: String, val emoji: String) {
    DO_FIRST("Hacer Ahora", "Urgente e Importante", "🔥"),
    SCHEDULE("Planificar", "Importante, No Urgente", "💎"),
    DELEGATE("Delegar", "Urgente, No Importante", "⚡"),
    ELIMINATE("Postergar", "Ni Urgente ni Importante", "🍃")
}

enum class KanbanColumn(val title: String, val emoji: String) {
    TODO("Por Hacer", "📋"),
    IN_PROGRESS("En Progreso", "🚀"),
    DONE("Completadas", "✅")
}

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val labelId: Long? = null,
    val dueDateMillis: Long = System.currentTimeMillis(),
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val isCompleted: Boolean = false,
    val reminderMinutesBefore: Int = 15, // 0 = exact time, 15 = 15 min before, -1 = no reminder
    val kanbanStatus: String = "TODO", // "TODO", "IN_PROGRESS", "DONE"
    val eisenhowerQuadrant: String = "DO_FIRST", // "DO_FIRST", "SCHEDULE", "DELEGATE", "ELIMINATE"
    val recurrenceInterval: String = "NONE", // "NONE", "DAILY", "WEEKLY", "MONTHLY", "YEARLY"
    val createdAtMillis: Long = System.currentTimeMillis()
)
