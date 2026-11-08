package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.provider.PeruHolidaysProvider
import com.example.ui.components.EventItemCard
import com.example.ui.components.PeruHolidayBanner
import com.example.ui.components.TaskItemCard
import com.example.ui.viewmodel.AgendaUiState
import com.example.ui.viewmodel.AgendaViewModel
import com.example.util.GoogleCalendarHelper
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Calendar
import java.util.Locale

enum class CalendarViewMode(val label: String) {
    MONTH("Mes"),
    WEEK("Semana"),
    DAY("Día")
}

@Composable
fun CalendarScreen(
    uiState: AgendaUiState,
    viewModel: AgendaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var viewMode by remember { mutableStateOf(CalendarViewMode.MONTH) }
    var currentYearMonth by remember { mutableStateOf(YearMonth.from(uiState.selectedDate)) }

    val daysOfWeek = listOf("Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb")
    val zone = ZoneId.systemDefault()

    // Fechas calculadas
    val firstDayOfMonth = currentYearMonth.atDay(1)
    val dayOfWeekOffset = firstDayOfMonth.dayOfWeek.value % 7 // 0 for Sunday
    val daysInMonth = currentYearMonth.lengthOfMonth()

    val totalSlots = dayOfWeekOffset + daysInMonth
    val calendarDays = (1..totalSlots).map { slot ->
        if (slot > dayOfWeekOffset) {
            val day = slot - dayOfWeekOffset
            currentYearMonth.atDay(day)
        } else {
            null
        }
    }

    val monthTitleFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale("es", "PE"))
    val monthTitle = currentYearMonth.format(monthTitleFormatter).replaceFirstChar { it.uppercase() }

    val selStart = uiState.selectedDate.atStartOfDay(zone).toInstant().toEpochMilli()
    val selEnd = uiState.selectedDate.atTime(23, 59, 59).atZone(zone).toInstant().toEpochMilli()

    val dayEvents = uiState.filteredEvents.filter {
        (it.startMillis in selStart..selEnd) || (it.isAllDay && it.endMillis >= selStart && it.startMillis <= selEnd)
    }

    val dayTasks = uiState.filteredTasks.filter {
        it.dueDateMillis in selStart..selEnd
    }

    val selectedDayHoliday = PeruHolidaysProvider.isHoliday(uiState.selectedDate)

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Selector de Modo: Mes / Semana / Día
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Píldoras de vista Mes / Semana / Día
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(4.dp)
                    ) {
                        CalendarViewMode.values().forEach { mode ->
                            val isSelected = viewMode == mode
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                                    .clickable { viewMode = mode }
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                                    .testTag("calendar_view_${mode.name.lowercase()}")
                            ) {
                                Text(
                                    text = mode.label,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Botón para exportar a Google Calendar / ICS
                    IconButton(
                        onClick = {
                            val intent = GoogleCalendarHelper.generateIcsFileAndShare(context, uiState.events)
                            context.startActivity(android.content.Intent.createChooser(intent, "Sincronizar calendario"))
                        },
                        modifier = Modifier.testTag("sync_calendar_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Exportar a Google Calendar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // CONTENIDO SEGÚN MODO DE VISTA
            when (viewMode) {
                CalendarViewMode.MONTH -> {
                    // Navegación de mes
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(
                                onClick = { currentYearMonth = currentYearMonth.minusMonths(1) },
                                modifier = Modifier.testTag("prev_month_btn")
                            ) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Mes anterior")
                            }

                            Text(
                                text = monthTitle,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            IconButton(
                                onClick = { currentYearMonth = currentYearMonth.plusMonths(1) },
                                modifier = Modifier.testTag("next_month_btn")
                            ) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Mes siguiente")
                            }
                        }
                    }

                    // Cuadrícula Mensual
                    item {
                        Card(
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceAround,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    daysOfWeek.forEach { dayName ->
                                        Text(
                                            text = dayName,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = if (dayName == "Dom") Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                val rows = calendarDays.chunked(7)
                                rows.forEach { week ->
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceAround,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                    ) {
                                        week.forEach { date ->
                                            if (date != null) {
                                                val isSelected = date == uiState.selectedDate
                                                val isToday = date == LocalDate.now()
                                                val holiday = PeruHolidaysProvider.isHoliday(date)

                                                val dStart = date.atStartOfDay(zone).toInstant().toEpochMilli()
                                                val dEnd = date.atTime(23, 59, 59).atZone(zone).toInstant().toEpochMilli()
                                                val hasEvents = uiState.events.any {
                                                    (it.startMillis in dStart..dEnd) || (it.isAllDay && it.endMillis >= dStart && it.startMillis <= dEnd)
                                                }

                                                val cellBg = when {
                                                    isSelected -> MaterialTheme.colorScheme.primary
                                                    holiday != null -> Color(0xFFFEE2E2)
                                                    isToday -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                                                    else -> Color.Transparent
                                                }

                                                val textColor = when {
                                                    isSelected -> MaterialTheme.colorScheme.onPrimary
                                                    holiday != null -> Color(0xFFB91C1C)
                                                    date.dayOfWeek.value == 7 -> Color(0xFFEF4444)
                                                    else -> MaterialTheme.colorScheme.onSurface
                                                }

                                                Box(
                                                    contentAlignment = Alignment.Center,
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .aspectRatio(1f)
                                                        .clip(CircleShape)
                                                        .background(cellBg)
                                                        .border(
                                                            width = if (isToday && !isSelected) 1.5.dp else 0.dp,
                                                            color = if (isToday && !isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                                            shape = CircleShape
                                                        )
                                                        .clickable { viewModel.selectDate(date) }
                                                        .testTag("calendar_day_${date.dayOfMonth}")
                                                ) {
                                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                        Text(
                                                            text = "${date.dayOfMonth}",
                                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                                fontWeight = if (isSelected || isToday || holiday != null) FontWeight.Bold else FontWeight.Normal
                                                            ),
                                                            color = textColor
                                                        )

                                                        if (holiday != null) {
                                                            Text(text = "🇵🇪", fontSize = 8.sp)
                                                        } else if (hasEvents && !isSelected) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .size(4.dp)
                                                                    .clip(CircleShape)
                                                                    .background(MaterialTheme.colorScheme.primary)
                                                            )
                                                        }
                                                    }
                                                }
                                            } else {
                                                Spacer(modifier = Modifier.weight(1f))
                                            }
                                        }
                                        if (week.size < 7) {
                                            for (i in 0 until (7 - week.size)) {
                                                Spacer(modifier = Modifier.weight(1f))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                CalendarViewMode.WEEK -> {
                    // Vista por Semana: Tira de 7 días y navegación semanal
                    val startOfWeek = uiState.selectedDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
                    val weekDays = (0..6).map { startOfWeek.plusDays(it.toLong()) }

                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(onClick = {
                                viewModel.selectDate(uiState.selectedDate.minusWeeks(1))
                            }) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Semana anterior")
                            }

                            val weekRangeTitle = "${startOfWeek.format(DateTimeFormatter.ofPattern("d MMM", Locale("es", "PE")))} - ${startOfWeek.plusDays(6).format(DateTimeFormatter.ofPattern("d MMM yyyy", Locale("es", "PE")))}"
                            Text(
                                text = weekRangeTitle.replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            IconButton(onClick = {
                                viewModel.selectDate(uiState.selectedDate.plusWeeks(1))
                            }) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Semana siguiente")
                            }
                        }
                    }

                    // Tira horizontal de días de la semana
                    item {
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceAround,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                weekDays.forEach { date ->
                                    val isSelected = date == uiState.selectedDate
                                    val isToday = date == LocalDate.now()
                                    val holiday = PeruHolidaysProvider.isHoliday(date)

                                    val bg = if (isSelected) MaterialTheme.colorScheme.primary else if (holiday != null) Color(0xFFFEE2E2) else Color.Transparent

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(bg)
                                            .clickable { viewModel.selectDate(date) }
                                            .padding(horizontal = 8.dp, vertical = 10.dp)
                                    ) {
                                        Text(
                                            text = date.format(DateTimeFormatter.ofPattern("EEE", Locale("es", "PE"))).take(3).uppercase(),
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "${date.dayOfMonth}",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else if (holiday != null) Color(0xFFB91C1C) else MaterialTheme.colorScheme.onSurface
                                        )
                                        if (holiday != null) {
                                            Text(text = "🇵🇪", fontSize = 10.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                CalendarViewMode.DAY -> {
                    // Vista por Día: Navegación de día en día con línea horaria
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(onClick = { viewModel.selectDate(uiState.selectedDate.minusDays(1)) }) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Día anterior")
                            }

                            val dayFormat = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM yyyy", Locale("es", "PE"))
                            Text(
                                text = uiState.selectedDate.format(dayFormat).replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            IconButton(onClick = { viewModel.selectDate(uiState.selectedDate.plusDays(1)) }) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Día siguiente")
                            }
                        }
                    }

                    // Línea horaria de 07:00 a 21:00
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Horario Diario",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                listOf(7, 9, 11, 13, 15, 17, 19, 21).forEach { hour ->
                                    val hourStr = String.format(Locale.getDefault(), "%02d:00", hour)
                                    val matchingEvents = dayEvents.filter { event ->
                                        val cal = Calendar.getInstance().apply { timeInMillis = event.startMillis }
                                        cal.get(Calendar.HOUR_OF_DAY) == hour
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = hourStr,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.width(46.dp)
                                        )

                                        if (matchingEvents.isNotEmpty()) {
                                            Column(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                                            ) {
                                                matchingEvents.forEach { ev ->
                                                    Text(
                                                        text = ev.title,
                                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                }
                                            }
                                        } else {
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(1.dp)
                                                    .background(MaterialTheme.colorScheme.outlineVariant)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Banner de feriado de Perú si aplica
            if (selectedDayHoliday != null) {
                item {
                    PeruHolidayBanner(holiday = selectedDayHoliday)
                }
            }

            // Encabezado del día seleccionado y botón Agendar
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 12.dp)
                    ) {
                        Text(
                            text = "Agenda del Día",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "${dayEvents.size} eventos • ${dayTasks.size} tareas",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = { viewModel.openCreateEventDialog() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("add_event_for_day_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Agendar", maxLines = 1)
                    }
                }
            }

            // Lista de eventos y tareas
            if (dayEvents.isEmpty() && dayTasks.isEmpty()) {
                item {
                    EmptyStatePlaceholder(
                        title = "Sin compromisos para esta fecha",
                        subtitle = "Toca 'Agendar' o el botón '+' para añadir una reunión o tarea."
                    )
                }
            } else {
                items(dayEvents, key = { it.id }) { event ->
                    val label = uiState.labels.find { it.id == event.labelId }
                    EventItemCard(
                        event = event,
                        label = label,
                        onEdit = { viewModel.openCreateEventDialog(event) },
                        onDelete = { viewModel.deleteEvent(event.id) }
                    )
                }

                items(dayTasks, key = { it.id }) { task ->
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
        }

        FloatingActionButton(
            onClick = { viewModel.openCreateEventDialog() },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("calendar_fab")
        ) {
            Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = "Nuevo Evento")
        }
    }
}
