package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LabelEntity
import com.example.data.model.TaskEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Quad(val emoji: String, val text: String, val bg: Color, val textColor: Color)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TaskItemCard(
    task: TaskEntity,
    label: LabelEntity?,
    onToggleCompleted: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onAdvanceKanban: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isDone = task.isCompleted || task.kanbanStatus == "DONE"
    val isInProgress = task.kanbanStatus == "IN_PROGRESS"

    val isDark = MaterialTheme.colorScheme.background.red < 0.5f
    val checkColor by animateColorAsState(
        targetValue = when {
            isDone -> MaterialTheme.colorScheme.primary
            isInProgress -> Color(0xFFF59E0B) // Ámbar de progreso
            else -> Color.Transparent
        },
        label = "checkColor"
    )

    // Fondo sofisticado y agradable en ambos modos:
    // En modo oscuro se usa la superficie elegante del tema con borde iluminado
    // En modo claro se usa un fondo sutil muy limpio con acento suave
    val cardBackground = when {
        isDone -> if (isDark) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        isInProgress -> if (isDark) MaterialTheme.colorScheme.surfaceVariant else Color(0xFFFFFDF5)
        else -> MaterialTheme.colorScheme.surface
    }

    val cardBorder = when {
        isInProgress -> androidx.compose.foundation.BorderStroke(
            1.5.dp,
            if (isDark) Color(0xFFFBBF24) else Color(0xFFF59E0B).copy(alpha = 0.6f)
        )
        isDone -> null
        else -> androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDark) MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f) else Color.Transparent
        )
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBackground
        ),
        border = cardBorder,
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDone) 0.dp else if (isInProgress) 3.dp else 1.5.dp),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .testTag("task_item_${task.id}")
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Indicador de ciclo interactivo: Por Hacer ➔ En Progreso ➔ Completada
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(checkColor)
                    .border(
                        width = if (!isDone && !isInProgress) 1.5.dp else 0.dp,
                        color = if (!isDone && !isInProgress) MaterialTheme.colorScheme.outline else Color.Transparent,
                        shape = CircleShape
                    )
                    .clickable { onToggleCompleted() }
                    .testTag("task_checkbox_${task.id}")
            ) {
                when {
                    isDone -> {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completada",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    isInProgress -> {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "En Progreso",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    else -> {
                        // Estado Por Hacer: círculo limpio
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.outlineVariant)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Contenido de la tarea
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onToggleCompleted() }
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    color = if (isDone) {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (task.description.isNotBlank()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                // FlowRow para que todas las etiquetas fluyan ordenadamente sin solaparse
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    // Etiqueta pastel personalizada
                    if (label != null) {
                        PastelBadge(label = label)
                    }

                    // Cuadrante Eisenhower con colores adaptables
                    val (eisenhowerEmoji, eisenhowerText, eisenhowerBg, eisenhowerTextColor) = when (task.eisenhowerQuadrant) {
                        "DO_FIRST" -> if (isDark) {
                            Quad("🔥", "Hacer Ya", Color(0xFF45191C), Color(0xFFFCA5A5))
                        } else {
                            Quad("🔥", "Hacer Ya", Color(0xFFFEE2E2), Color(0xFF991B1B))
                        }
                        "SCHEDULE" -> if (isDark) {
                            Quad("💎", "Planificar", Color(0xFF132F52), Color(0xFF93C5FD))
                        } else {
                            Quad("💎", "Planificar", Color(0xFFDBEAFE), Color(0xFF1E40AF))
                        }
                        "DELEGATE" -> if (isDark) {
                            Quad("⚡", "Delegar", Color(0xFF3F350B), Color(0xFFFEF08A))
                        } else {
                            Quad("⚡", "Delegar", Color(0xFFFEF3C7), Color(0xFF92400E))
                        }
                        else -> if (isDark) {
                            Quad("🍃", "Postergar", Color(0xFF0F3A22), Color(0xFF86EFAC))
                        } else {
                            Quad("🍃", "Postergar", Color(0xFFD1FAE5), Color(0xFF166534))
                        }
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(eisenhowerBg)
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "$eisenhowerEmoji $eisenhowerText",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                            ),
                            color = eisenhowerTextColor
                        )
                    }

                    // Estado de progreso interactivo con alto contraste y colores armoniosos
                    val (kanbanLabel, kanbanBg, kanbanTextColor) = when {
                        isDone -> if (isDark) {
                            Triple("✅ Hecho", Color(0xFF0F3A22), Color(0xFF86EFAC))
                        } else {
                            Triple("✅ Hecho", Color(0xFFDCFCE7), Color(0xFF166534))
                        }
                        isInProgress -> if (isDark) {
                            Triple("🚀 En Progreso (Toca p/ finalizar)", Color(0xFF3F2F0E), Color(0xFFFDE047))
                        } else {
                            Triple("🚀 En Progreso (Toca p/ finalizar)", Color(0xFFFEF3C7), Color(0xFF92400E))
                        }
                        else -> Triple(
                            "📋 Por Hacer (Toca p/ empezar)",
                            if (isDark) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f) else MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(kanbanBg)
                            .clickable { onToggleCompleted() }
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = kanbanLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                            ),
                            color = kanbanTextColor
                        )
                    }

                    // Badge de tarea recurrente (si aplica)
                    if (task.recurrenceInterval != "NONE") {
                        val (recIcon, recText) = when (task.recurrenceInterval) {
                            "DAILY" -> "🔄" to "Diaria"
                            "WEEKLY" -> "📅" to "Semanal"
                            "MONTHLY" -> "🗓️" to "Mensual"
                            "YEARLY" -> "🎂" to "Anual"
                            else -> "🔁" to task.recurrenceInterval
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9)
                                )
                                .padding(horizontal = 7.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "$recIcon $recText",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                                ),
                                color = if (isDark) Color(0xFF94A3B8) else Color(0xFF475569)
                            )
                        }
                    }

                    // Fecha y hora amigable de vencimiento programado
                    val friendlyDateString = com.example.util.TaskRecurrenceHelper.formatFriendlyDueDate(task.dueDateMillis)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Alarm,
                            contentDescription = "Recordatorio",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = friendlyDateString,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Botones de acción
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("edit_task_${task.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar tarea",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("delete_task_${task.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar tarea",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
