package com.example.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.EisenhowerQuadrant
import com.example.data.model.LabelEntity
import com.example.data.model.TaskEntity
import com.example.data.model.TaskPriority
import com.example.data.model.TaskRecurrence
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateTaskDialog(
    taskToEdit: TaskEntity?,
    labels: List<LabelEntity>,
    onSaveTask: (TaskEntity) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val zone = ZoneId.systemDefault()

    var title by remember { mutableStateOf(taskToEdit?.title ?: "") }
    var description by remember { mutableStateOf(taskToEdit?.description ?: "") }
    var selectedLabelId by remember { mutableStateOf<Long?>(taskToEdit?.labelId ?: labels.firstOrNull()?.id) }
    var priority by remember { mutableStateOf(taskToEdit?.priority ?: TaskPriority.MEDIUM) }
    var reminderMinutes by remember { mutableStateOf(taskToEdit?.reminderMinutesBefore ?: 15) }
    var eisenhowerQuadrant by remember { mutableStateOf(taskToEdit?.eisenhowerQuadrant ?: "DO_FIRST") }
    var kanbanStatus by remember { mutableStateOf(taskToEdit?.kanbanStatus ?: "TODO") }
    var recurrenceInterval by remember { mutableStateOf(taskToEdit?.recurrenceInterval ?: "NONE") }

    // Fecha y hora de vencimiento
    val initialDateTime = remember(taskToEdit) {
        if (taskToEdit != null) {
            Instant.ofEpochMilli(taskToEdit.dueDateMillis).atZone(zone).toLocalDateTime()
        } else {
            LocalDateTime.now().plusHours(2).withMinute(0).withSecond(0)
        }
    }

    var selectedDate by remember { mutableStateOf(initialDateTime.toLocalDate()) }
    var selectedTime by remember { mutableStateOf(initialDateTime.toLocalTime()) }

    val dateFormatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM, yyyy", Locale("es", "PE"))
    val timeFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault())

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("create_task_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (taskToEdit == null) "Nueva Tarea Productiva" else "Editar Tarea",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título de la tarea o meta") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("task_title_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción o notas") },
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("task_description_input")
                )

                Spacer(modifier = Modifier.height(16.dp))

                // SECCIÓN: PROGRAMACIÓN ANTICIPADA (Fecha y Hora)
                Text(
                    text = "📅 Programación para Fecha Específica:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))

                // Selector de Fecha
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .clickable {
                            val dialog = DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                                },
                                selectedDate.year,
                                selectedDate.monthValue - 1,
                                selectedDate.dayOfMonth
                            )
                            dialog.show()
                        }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Fecha de vencimiento",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = selectedDate.format(dateFormatter).replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Atajos rápidos de fecha
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val today = LocalDate.now()
                    listOf(
                        "Hoy" to today,
                        "Mañana" to today.plusDays(1),
                        "+7 días" to today.plusWeeks(1),
                        "+1 mes" to today.plusMonths(1)
                    ).forEach { (shortcutLabel, targetDate) ->
                        val isSelected = selectedDate == targetDate
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { selectedDate = targetDate }
                                .padding(vertical = 6.dp)
                        ) {
                            Text(
                                text = shortcutLabel,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Medium),
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Selector de Hora
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .clickable {
                            val timeDialog = TimePickerDialog(
                                context,
                                { _, hourOfDay, minute ->
                                    selectedTime = LocalTime.of(hourOfDay, minute)
                                },
                                selectedTime.hour,
                                selectedTime.minute,
                                false
                            )
                            timeDialog.show()
                        }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Hora límite",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = selectedTime.format(timeFormatter),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // SECCIÓN: TAREAS REPETITIVAS / RECURRENTES
                Text(
                    text = "🔁 Repetición Periódica (Recurrencia):",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Al completar la tarea, avanzará automáticamente a la siguiente fecha programada.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TaskRecurrence.values().forEach { rec ->
                        val isSelected = recurrenceInterval == rec.name
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { recurrenceInterval = rec.name }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = "${rec.icon} ${rec.label}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Matriz de Eisenhower
                Text(
                    text = "Matriz de Eisenhower (Productividad):",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                        EisenhowerQuadrantOption(
                            quadrant = EisenhowerQuadrant.DO_FIRST,
                            isSelected = eisenhowerQuadrant == "DO_FIRST",
                            onClick = { eisenhowerQuadrant = "DO_FIRST" },
                            modifier = Modifier.weight(1f)
                        )
                        EisenhowerQuadrantOption(
                            quadrant = EisenhowerQuadrant.SCHEDULE,
                            isSelected = eisenhowerQuadrant == "SCHEDULE",
                            onClick = { eisenhowerQuadrant = "SCHEDULE" },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                        EisenhowerQuadrantOption(
                            quadrant = EisenhowerQuadrant.DELEGATE,
                            isSelected = eisenhowerQuadrant == "DELEGATE",
                            onClick = { eisenhowerQuadrant = "DELEGATE" },
                            modifier = Modifier.weight(1f)
                        )
                        EisenhowerQuadrantOption(
                            quadrant = EisenhowerQuadrant.ELIMINATE,
                            isSelected = eisenhowerQuadrant == "ELIMINATE",
                            onClick = { eisenhowerQuadrant = "ELIMINATE" },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Estado Kanban
                Text(
                    text = "Estado Tablero Kanban:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf(
                        "TODO" to "📋 Por Hacer",
                        "IN_PROGRESS" to "🚀 En Progreso",
                        "DONE" to "✅ Completada"
                    ).forEach { (status, labelText) ->
                        val isSelected = kanbanStatus == status
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { kanbanStatus = status }
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = labelText,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Etiqueta:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    labels.forEach { label ->
                        PastelBadge(
                            label = label,
                            isSelected = selectedLabelId == label.id,
                            onClick = { selectedLabelId = label.id }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Recordatorio Inteligente con Sonido:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf(
                        0 to "Exacto",
                        15 to "15 min",
                        30 to "30 min",
                        60 to "1 hora"
                    ).forEach { (mins, text) ->
                        val isSelected = reminderMinutes == mins
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { reminderMinutes = mins }
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = text,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            val computedDueDateMillis = selectedDate.atTime(selectedTime)
                                .atZone(zone)
                                .toInstant()
                                .toEpochMilli()

                            val isCompletedVal = kanbanStatus == "DONE"
                            val task = taskToEdit?.copy(
                                title = title.trim(),
                                description = description.trim(),
                                labelId = selectedLabelId,
                                priority = priority,
                                reminderMinutesBefore = reminderMinutes,
                                eisenhowerQuadrant = eisenhowerQuadrant,
                                kanbanStatus = kanbanStatus,
                                isCompleted = isCompletedVal,
                                recurrenceInterval = recurrenceInterval,
                                dueDateMillis = computedDueDateMillis
                            ) ?: TaskEntity(
                                title = title.trim(),
                                description = description.trim(),
                                labelId = selectedLabelId,
                                priority = priority,
                                reminderMinutesBefore = reminderMinutes,
                                eisenhowerQuadrant = eisenhowerQuadrant,
                                kanbanStatus = kanbanStatus,
                                isCompleted = isCompletedVal,
                                recurrenceInterval = recurrenceInterval,
                                dueDateMillis = computedDueDateMillis
                            )
                            onSaveTask(task)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_task_btn")
                ) {
                    Text(if (taskToEdit == null) "Crear Tarea" else "Guardar Cambios")
                }
            }
        }
    }
}

@Composable
fun EisenhowerQuadrantOption(
    quadrant: EisenhowerQuadrant,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Column {
            Text(
                text = "${quadrant.emoji} ${quadrant.title}",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = quadrant.subtitle,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
