package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.AlarmEntity
import com.example.data.model.EventEntity
import com.example.data.model.LabelEntity
import com.example.data.model.TaskEntity
import com.example.data.provider.PeruHolidaysProvider
import com.example.data.repository.AgendaRepository
import com.example.util.AlarmScheduler
import com.example.util.AlarmSoundPlayer
import com.example.util.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class AgendaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AgendaRepository
    private val _uiState = MutableStateFlow(AgendaUiState())
    val uiState: StateFlow<AgendaUiState>

    init {
        val db = AppDatabase.getDatabase(application)
        repository = AgendaRepository(db.agendaDao())

        val upcomingHoliday = PeruHolidaysProvider.getNextUpcomingHoliday()
        _uiState.update { it.copy(upcomingPeruHoliday = upcomingHoliday) }

        uiState = combine(
            _uiState,
            repository.allLabels,
            repository.allTasks,
            repository.allEvents,
            repository.allAlarms
        ) { currentUi, labels, tasks, events, alarms ->
            currentUi.copy(
                labels = labels,
                tasks = tasks,
                events = events,
                alarms = alarms
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AgendaUiState(upcomingPeruHoliday = upcomingHoliday)
        )

        // Asegurar feriados peruanos en la base de datos
        viewModelScope.launch {
            repository.ensurePeruHolidays(LocalDate.now().year)
        }

        // Comprobación automática de actualizaciones desde GitHub Releases
        checkForAppUpdates()
    }

    fun checkForAppUpdates() {
        viewModelScope.launch {
            val updateInfo = com.example.util.AppUpdateManager.checkForUpdates(
                currentVersionName = com.example.BuildConfig.VERSION_NAME
            )
            if (updateInfo != null && updateInfo.hasUpdate) {
                _uiState.update { it.copy(availableUpdate = updateInfo) }
            }
        }
    }

    fun dismissUpdateDialog() {
        _uiState.update { it.copy(availableUpdate = null) }
    }

    fun selectTab(tab: AgendaTab) {
        _uiState.update { it.copy(activeTab = tab) }
    }

    fun selectDate(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date) }
    }

    fun selectLabelFilter(labelId: Long?) {
        _uiState.update {
            val newFilter = if (it.selectedLabelFilterId == labelId) null else labelId
            it.copy(selectedLabelFilterId = newFilter)
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        _uiState.update { it.copy(themeMode = mode) }
    }

    // Tareas con ciclo progresivo: Por Hacer ➔ En Progreso ➔ Completada
    fun toggleTask(task: TaskEntity) {
        cycleTaskState(task)
    }

    fun cycleTaskState(task: TaskEntity) {
        viewModelScope.launch {
            val currentStatus = if (task.isCompleted && task.kanbanStatus != "DONE") "DONE" else task.kanbanStatus
            val (nextStatus, isCompleted) = when (currentStatus) {
                "TODO" -> "IN_PROGRESS" to false
                "IN_PROGRESS" -> {
                    // Sonido y notificación de confirmación de tarea completada
                    AlarmSoundPlayer.playTaskReminder(getApplication<Application>())
                    NotificationHelper.showTaskReminderNotification(
                        getApplication<Application>(),
                        task.id,
                        "¡Tarea Completada con Éxito! 🎉",
                        "Has finalizado '${task.title}'. +15 Puntos de Productividad"
                    )
                    "DONE" to true
                }
                else -> "TODO" to false
            }
            repository.updateTask(task.copy(kanbanStatus = nextStatus, isCompleted = isCompleted))
        }
    }

    fun advanceTaskKanban(task: TaskEntity) {
        cycleTaskState(task)
    }

    fun moveTaskToKanban(task: TaskEntity, targetStatus: String) {
        viewModelScope.launch {
            val isCompleted = targetStatus == "DONE"
            repository.updateTask(task.copy(kanbanStatus = targetStatus, isCompleted = isCompleted))
        }
    }

    fun moveTaskToEisenhower(task: TaskEntity, targetQuadrant: String) {
        viewModelScope.launch {
            repository.updateTask(task.copy(eisenhowerQuadrant = targetQuadrant))
        }
    }

    fun saveTask(task: TaskEntity) {
        viewModelScope.launch {
            if (task.id == 0L) {
                val newId = repository.insertTask(task)
                val insertedTask = task.copy(id = newId)
                AlarmScheduler.scheduleTaskReminder(getApplication(), insertedTask)
            } else {
                repository.updateTask(task)
                AlarmScheduler.scheduleTaskReminder(getApplication(), task)
            }
            closeCreateTaskDialog()
        }
    }

    fun deleteTask(id: Long) {
        viewModelScope.launch {
            repository.deleteTask(id)
        }
    }

    // Eventos
    fun saveEvent(event: EventEntity) {
        viewModelScope.launch {
            if (event.id == 0L) {
                repository.insertEvent(event)
            } else {
                repository.updateEvent(event)
            }
            closeCreateEventDialog()
        }
    }

    fun deleteEvent(id: Long) {
        viewModelScope.launch {
            repository.deleteEvent(id)
        }
    }

    // Etiquetas personalizables
    fun saveLabel(label: LabelEntity) {
        viewModelScope.launch {
            if (label.id == 0L) {
                repository.insertLabel(label)
            } else {
                repository.updateLabel(label)
            }
        }
    }

    fun deleteLabel(id: Long) {
        viewModelScope.launch {
            repository.deleteLabel(id)
            if (_uiState.value.selectedLabelFilterId == id) {
                _uiState.update { it.copy(selectedLabelFilterId = null) }
            }
        }
    }

    // Alarmas
    fun toggleAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            val updated = alarm.copy(isEnabled = !alarm.isEnabled)
            repository.updateAlarm(updated)
            if (updated.isEnabled) {
                AlarmScheduler.scheduleAlarm(getApplication(), updated)
            } else {
                AlarmScheduler.cancelAlarm(getApplication(), updated.id)
            }
        }
    }

    fun saveAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            val savedAlarm = if (alarm.id == 0L) {
                val newId = repository.insertAlarm(alarm)
                alarm.copy(id = newId)
            } else {
                repository.updateAlarm(alarm)
                alarm
            }
            if (savedAlarm.isEnabled) {
                AlarmScheduler.scheduleAlarm(getApplication(), savedAlarm)
            }
            closeCreateAlarmDialog()
        }
    }

    fun deleteAlarm(id: Long) {
        viewModelScope.launch {
            AlarmScheduler.cancelAlarm(getApplication(), id)
            repository.deleteAlarm(id)
        }
    }

    fun triggerAlarm(alarm: AlarmEntity) {
        AlarmSoundPlayer.playAlarm(getApplication(), alarm.soundUri, alarm.vibrate)
        _uiState.update { it.copy(activeAlarmRinging = alarm) }
    }

    fun dismissActiveAlarm() {
        AlarmSoundPlayer.stop()
        _uiState.update { it.copy(activeAlarmRinging = null) }
    }

    // Diálogos UI
    fun openCreateTaskDialog(taskToEdit: TaskEntity? = null) {
        _uiState.update { it.copy(showCreateTaskDialog = true, editingTask = taskToEdit) }
    }

    fun closeCreateTaskDialog() {
        _uiState.update { it.copy(showCreateTaskDialog = false, editingTask = null) }
    }

    fun openCreateEventDialog(eventToEdit: EventEntity? = null) {
        _uiState.update { it.copy(showCreateEventDialog = true, editingEvent = eventToEdit) }
    }

    fun closeCreateEventDialog() {
        _uiState.update { it.copy(showCreateEventDialog = false, editingEvent = null) }
    }

    fun openCreateAlarmDialog(alarmToEdit: AlarmEntity? = null) {
        _uiState.update { it.copy(showCreateAlarmDialog = true, editingAlarm = alarmToEdit) }
    }

    fun closeCreateAlarmDialog() {
        _uiState.update { it.copy(showCreateAlarmDialog = false, editingAlarm = null) }
    }

    fun openLabelManagerDialog() {
        _uiState.update { it.copy(showLabelManagerDialog = true) }
    }

    fun closeLabelManagerDialog() {
        _uiState.update { it.copy(showLabelManagerDialog = false) }
    }
}
