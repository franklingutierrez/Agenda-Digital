package com.example.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LabelEntity
import com.example.ui.components.EventItemCard
import com.example.ui.components.PastelBadge
import com.example.ui.components.PeruHolidayBanner
import com.example.ui.components.TaskItemCard
import com.example.ui.viewmodel.AgendaUiState
import com.example.ui.viewmodel.AgendaViewModel
import com.example.ui.viewmodel.ThemeMode
import com.example.util.GoogleCalendarHelper
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun DashboardScreen(
    uiState: AgendaUiState,
    viewModel: AgendaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val today = LocalDate.now()
    val zone = ZoneId.systemDefault()
    val todayStart = today.atStartOfDay(zone).toInstant().toEpochMilli()
    val todayEnd = today.atTime(23, 59, 59).atZone(zone).toInstant().toEpochMilli()

    // Eventos y tareas de hoy con filtro
    val todayEvents = uiState.filteredEvents.filter {
        (it.startMillis in todayStart..todayEnd) || (it.isAllDay && it.endMillis >= todayStart && it.startMillis <= todayEnd)
    }

    val todayTasks = uiState.filteredTasks

    val dateFormatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", Locale("es", "PE"))
    val dateString = today.format(dateFormatter).replaceFirstChar { it.uppercase() }

    val nextAlarm = uiState.alarms.firstOrNull { it.isEnabled }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Cabecera de bienvenida con modo oscuro y sincronización
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "Mi Agenda Diaria",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = dateString,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row {
                        // Exportar ICS / Sincronizar Google Calendar
                        IconButton(
                            onClick = {
                                val intent = GoogleCalendarHelper.generateIcsFileAndShare(context, uiState.events)
                                context.startActivity(IntentChooser(context, intent))
                            },
                            modifier = Modifier.testTag("share_calendar_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Exportar a Google Calendar",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Selector de Modo Oscuro
                        IconButton(
                            onClick = {
                                val nextMode = when (uiState.themeMode) {
                                    ThemeMode.SYSTEM -> ThemeMode.DARK
                                    ThemeMode.DARK -> ThemeMode.LIGHT
                                    ThemeMode.LIGHT -> ThemeMode.SYSTEM
                                }
                                viewModel.setThemeMode(nextMode)
                            },
                            modifier = Modifier.testTag("theme_toggle_btn")
                        ) {
                            val icon = when (uiState.themeMode) {
                                ThemeMode.SYSTEM -> Icons.Default.WbSunny
                                ThemeMode.DARK -> Icons.Default.DarkMode
                                ThemeMode.LIGHT -> Icons.Default.LightMode
                            }
                            Icon(
                                imageVector = icon,
                                contentDescription = "Cambiar tema",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Banner del Próximo Feriado Nacional de Perú
            uiState.upcomingPeruHoliday?.let { holiday ->
                item {
                    PeruHolidayBanner(holiday = holiday)
                }
            }

            // Tarjeta de Productividad y Despertador
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    text = "Rendimiento del Día",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${uiState.todayCompletedCount} de ${uiState.totalTasksCount} tareas completadas",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Box(contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(
                                    progress = { uiState.productivityPercentage },
                                    color = MaterialTheme.colorScheme.secondary,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.size(46.dp),
                                    strokeWidth = 5.dp
                                )
                                Text(
                                    text = "${(uiState.productivityPercentage * 100).toInt()}%",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        LinearProgressIndicator(
                            progress = { uiState.productivityPercentage },
                            color = MaterialTheme.colorScheme.secondary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape)
                        )

                        if (nextAlarm != null) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f))
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Alarm,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Despertador activo: ${nextAlarm.label} a las ${String.format(Locale.getDefault(), "%02d:%02d", nextAlarm.hour, nextAlarm.minute)}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // Barra horizontal de etiquetas personalizables con colores
            item {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Etiquetas de Organización",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Text(
                            text = "+ Gestionar",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier
                                .clickable { viewModel.openLabelManagerDialog() }
                                .padding(4.dp)
                                .testTag("manage_labels_btn")
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        // Opción "Todas"
                        val isAllSelected = uiState.selectedLabelFilterId == null
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isAllSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { viewModel.selectLabelFilter(null) }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                .testTag("filter_all_labels")
                        ) {
                            Text(
                                text = "Todas",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isAllSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        uiState.labels.forEach { label ->
                            PastelBadge(
                                label = label,
                                isSelected = uiState.selectedLabelFilterId == label.id,
                                onClick = { viewModel.selectLabelFilter(label.id) },
                                modifier = Modifier.testTag("filter_label_${label.id}")
                            )
                        }
                    }
                }
            }

            // Sección: Tareas de Hoy
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Tareas del Día (${todayTasks.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    IconButton(
                        onClick = { viewModel.openCreateTaskDialog() },
                        modifier = Modifier.testTag("add_task_inline_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Nueva Tarea",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            if (todayTasks.isEmpty()) {
                item {
                    EmptyStatePlaceholder(
                        title = "Sin tareas pendientes",
                        subtitle = "¡Todo al día! Pulsa el botón '+' para agregar una nueva meta."
                    )
                }
            } else {
                items(todayTasks, key = { it.id }) { task ->
                    val label = uiState.labels.find { it.id == task.labelId }
                    TaskItemCard(
                        task = task,
                        label = label,
                        onToggleCompleted = { viewModel.toggleTask(task) },
                        onEdit = { viewModel.openCreateTaskDialog(task) },
                        onDelete = { viewModel.deleteTask(task.id) },
                        onAdvanceKanban = { viewModel.advanceTaskKanban(task) }
                    )
                }
            }

            // Sección: Eventos de Hoy
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text(
                        text = "Compromisos y Eventos (${todayEvents.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    IconButton(
                        onClick = { viewModel.openCreateEventDialog() },
                        modifier = Modifier.testTag("add_event_inline_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Nuevo Evento",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            if (todayEvents.isEmpty()) {
                item {
                    EmptyStatePlaceholder(
                        title = "Sin eventos hoy",
                        subtitle = "No tienes reuniones programadas para hoy."
                    )
                }
            } else {
                items(todayEvents, key = { it.id }) { event ->
                    val label = uiState.labels.find { it.id == event.labelId }
                    EventItemCard(
                        event = event,
                        label = label,
                        onEdit = { viewModel.openCreateEventDialog(event) },
                        onDelete = { viewModel.deleteEvent(event.id) }
                    )
                }
            }
        }

        // FAB rápido para nueva tarea o evento
        FloatingActionButton(
            onClick = { viewModel.openCreateTaskDialog() },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("dashboard_fab")
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Añadir tarea o evento")
        }
    }
}

private fun IntentChooser(context: Context, intent: android.content.Intent): android.content.Intent {
    return android.content.Intent.createChooser(intent, "Compartir o Sincronizar Calendario")
}

@Composable
fun EmptyStatePlaceholder(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
