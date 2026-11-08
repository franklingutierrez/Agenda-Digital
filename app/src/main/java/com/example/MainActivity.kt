package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AlarmEntity
import com.example.ui.components.AlarmRingingDialog
import com.example.ui.components.CreateAlarmDialog
import com.example.ui.components.CreateEventDialog
import com.example.ui.components.CreateTaskDialog
import com.example.ui.components.LabelManagerDialog
import com.example.ui.screens.AlarmsScreen
import com.example.ui.screens.CalendarScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.TasksScreen
import com.example.ui.theme.AgendaTheme
import com.example.ui.viewmodel.AgendaTab
import com.example.ui.viewmodel.AgendaViewModel
import com.example.ui.viewmodel.ThemeMode
import com.example.util.NotificationHelper

class MainActivity : ComponentActivity() {

    private val viewModel: AgendaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicializar canales de notificación para alarmas y tareas
        NotificationHelper.createNotificationChannels(this)

        // Comprobar si se abrió desde una notificación de alarma
        val triggeredAlarmId = intent?.getLongExtra("ALARM_TRIGGERED_ID", -1L) ?: -1L
        if (triggeredAlarmId != -1L) {
            val title = intent?.getStringExtra("ALARM_TITLE") ?: "Despertador"
            viewModel.triggerAlarm(
                AlarmEntity(
                    id = triggeredAlarmId,
                    hour = 7,
                    minute = 0,
                    label = title,
                    isEnabled = true
                )
            )
        }

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            val isDarkTheme = when (uiState.themeMode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
            }

            AgendaTheme(darkTheme = isDarkTheme) {
                // Manejo de botón atrás para sub-pantallas
                if (uiState.activeTab != AgendaTab.DASHBOARD) {
                    BackHandler {
                        viewModel.selectTab(AgendaTab.DASHBOARD)
                    }
                }

                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val isWideScreen = maxWidth >= 600.dp

                    Scaffold(
                        contentWindowInsets = WindowInsets.safeDrawing,
                        bottomBar = {
                            if (!isWideScreen) {
                                AgendaBottomNavBar(
                                    currentTab = uiState.activeTab,
                                    onSelectTab = { viewModel.selectTab(it) }
                                )
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    ) { innerPadding ->
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            if (isWideScreen) {
                                AgendaNavRail(
                                    currentTab = uiState.activeTab,
                                    onSelectTab = { viewModel.selectTab(it) }
                                )
                            }

                            Box(modifier = Modifier.fillMaxSize()) {
                                when (uiState.activeTab) {
                                    AgendaTab.DASHBOARD -> DashboardScreen(
                                        uiState = uiState,
                                        viewModel = viewModel
                                    )
                                    AgendaTab.CALENDAR -> CalendarScreen(
                                        uiState = uiState,
                                        viewModel = viewModel
                                    )
                                    AgendaTab.TASKS -> TasksScreen(
                                        uiState = uiState,
                                        viewModel = viewModel
                                    )
                                    AgendaTab.ALARMS -> AlarmsScreen(
                                        uiState = uiState,
                                        viewModel = viewModel
                                    )
                                }
                            }
                        }

                        // Diálogos modales
                        if (uiState.showLabelManagerDialog) {
                            LabelManagerDialog(
                                labels = uiState.labels,
                                onSaveLabel = { viewModel.saveLabel(it) },
                                onDeleteLabel = { viewModel.deleteLabel(it) },
                                onDismiss = { viewModel.closeLabelManagerDialog() }
                            )
                        }

                        if (uiState.showCreateTaskDialog) {
                            CreateTaskDialog(
                                taskToEdit = uiState.editingTask,
                                labels = uiState.labels,
                                onSaveTask = { viewModel.saveTask(it) },
                                onDismiss = { viewModel.closeCreateTaskDialog() }
                            )
                        }

                        if (uiState.showCreateEventDialog) {
                            CreateEventDialog(
                                selectedDate = uiState.selectedDate,
                                eventToEdit = uiState.editingEvent,
                                labels = uiState.labels,
                                onSaveEvent = { viewModel.saveEvent(it) },
                                onDismiss = { viewModel.closeCreateEventDialog() }
                            )
                        }

                        if (uiState.showCreateAlarmDialog) {
                            CreateAlarmDialog(
                                alarmToEdit = uiState.editingAlarm,
                                onSaveAlarm = { viewModel.saveAlarm(it) },
                                onDismiss = { viewModel.closeCreateAlarmDialog() }
                            )
                        }

                        uiState.activeAlarmRinging?.let { ringingAlarm ->
                            AlarmRingingDialog(
                                alarm = ringingAlarm,
                                todayTasksPendingCount = uiState.tasks.count { !it.isCompleted },
                                onSnooze = {
                                    viewModel.dismissActiveAlarm()
                                },
                                onDismiss = {
                                    viewModel.dismissActiveAlarm()
                                }
                            )
                        }

                        // Notificación en pantalla de nueva actualización disponible
                        uiState.availableUpdate?.let { updateInfo ->
                            com.example.ui.components.AppUpdateDialog(
                                updateInfo = updateInfo,
                                onDismiss = { viewModel.dismissUpdateDialog() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AgendaBottomNavBar(
    currentTab: AgendaTab,
    onSelectTab: (AgendaTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("agenda_bottom_nav")
    ) {
        NavigationBarItem(
            selected = currentTab == AgendaTab.DASHBOARD,
            onClick = { onSelectTab(AgendaTab.DASHBOARD) },
            icon = {
                Icon(
                    imageVector = if (currentTab == AgendaTab.DASHBOARD) Icons.Filled.Dashboard else Icons.Outlined.Dashboard,
                    contentDescription = "Panel"
                )
            },
            label = { Text("Panel") },
            modifier = Modifier.testTag("nav_dashboard")
        )

        NavigationBarItem(
            selected = currentTab == AgendaTab.CALENDAR,
            onClick = { onSelectTab(AgendaTab.CALENDAR) },
            icon = {
                Icon(
                    imageVector = if (currentTab == AgendaTab.CALENDAR) Icons.Filled.CalendarMonth else Icons.Outlined.CalendarMonth,
                    contentDescription = "Calendario"
                )
            },
            label = { Text("Calendario") },
            modifier = Modifier.testTag("nav_calendar")
        )

        NavigationBarItem(
            selected = currentTab == AgendaTab.TASKS,
            onClick = { onSelectTab(AgendaTab.TASKS) },
            icon = {
                Icon(
                    imageVector = if (currentTab == AgendaTab.TASKS) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                    contentDescription = "Tareas"
                )
            },
            label = { Text("Tareas") },
            modifier = Modifier.testTag("nav_tasks")
        )

        NavigationBarItem(
            selected = currentTab == AgendaTab.ALARMS,
            onClick = { onSelectTab(AgendaTab.ALARMS) },
            icon = {
                Icon(
                    imageVector = if (currentTab == AgendaTab.ALARMS) Icons.Filled.Alarm else Icons.Outlined.Alarm,
                    contentDescription = "Alarma"
                )
            },
            label = { Text("Alarma") },
            modifier = Modifier.testTag("nav_alarms")
        )
    }
}

@Composable
fun AgendaNavRail(
    currentTab: AgendaTab,
    onSelectTab: (AgendaTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationRail(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier
            .fillMaxHeight()
            .testTag("agenda_nav_rail")
    ) {
        NavigationRailItem(
            selected = currentTab == AgendaTab.DASHBOARD,
            onClick = { onSelectTab(AgendaTab.DASHBOARD) },
            icon = {
                Icon(
                    imageVector = if (currentTab == AgendaTab.DASHBOARD) Icons.Filled.Dashboard else Icons.Outlined.Dashboard,
                    contentDescription = "Panel"
                )
            },
            label = { Text("Panel") }
        )

        NavigationRailItem(
            selected = currentTab == AgendaTab.CALENDAR,
            onClick = { onSelectTab(AgendaTab.CALENDAR) },
            icon = {
                Icon(
                    imageVector = if (currentTab == AgendaTab.CALENDAR) Icons.Filled.CalendarMonth else Icons.Outlined.CalendarMonth,
                    contentDescription = "Calendario"
                )
            },
            label = { Text("Calendario") }
        )

        NavigationRailItem(
            selected = currentTab == AgendaTab.TASKS,
            onClick = { onSelectTab(AgendaTab.TASKS) },
            icon = {
                Icon(
                    imageVector = if (currentTab == AgendaTab.TASKS) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                    contentDescription = "Tareas"
                )
            },
            label = { Text("Tareas") }
        )

        NavigationRailItem(
            selected = currentTab == AgendaTab.ALARMS,
            onClick = { onSelectTab(AgendaTab.ALARMS) },
            icon = {
                Icon(
                    imageVector = if (currentTab == AgendaTab.ALARMS) Icons.Filled.Alarm else Icons.Outlined.Alarm,
                    contentDescription = "Alarma"
                )
            },
            label = { Text("Alarma") }
        )
    }
}
