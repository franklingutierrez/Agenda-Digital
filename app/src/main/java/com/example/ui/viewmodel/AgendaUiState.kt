package com.example.ui.viewmodel

import com.example.data.model.AlarmEntity
import com.example.data.model.EventEntity
import com.example.data.model.LabelEntity
import com.example.data.model.TaskEntity
import com.example.data.provider.PeruHoliday
import java.time.LocalDate

enum class AgendaTab {
    DASHBOARD,
    CALENDAR,
    TASKS,
    ALARMS
}

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

data class AgendaUiState(
    val labels: List<LabelEntity> = emptyList(),
    val tasks: List<TaskEntity> = emptyList(),
    val events: List<EventEntity> = emptyList(),
    val alarms: List<AlarmEntity> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now(),
    val selectedLabelFilterId: Long? = null,
    val activeTab: AgendaTab = AgendaTab.DASHBOARD,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val upcomingPeruHoliday: PeruHoliday? = null,
    val activeAlarmRinging: AlarmEntity? = null,
    val showCreateTaskDialog: Boolean = false,
    val showCreateEventDialog: Boolean = false,
    val showCreateAlarmDialog: Boolean = false,
    val showLabelManagerDialog: Boolean = false,
    val editingTask: TaskEntity? = null,
    val editingEvent: EventEntity? = null,
    val editingAlarm: AlarmEntity? = null,
    val isGoogleCalendarSyncing: Boolean = false,
    val availableUpdate: com.example.util.AppUpdateInfo? = null
) {
    val today: LocalDate = LocalDate.now()

    val filteredTasks: List<TaskEntity>
        get() = tasks.filter { task ->
            if (selectedLabelFilterId == null) true else task.labelId == selectedLabelFilterId
        }

    val filteredEvents: List<EventEntity>
        get() = events.filter { event ->
            if (selectedLabelFilterId == null) true else event.labelId == selectedLabelFilterId
        }

    val todayCompletedCount: Int
        get() = tasks.count { it.isCompleted }

    val totalTasksCount: Int
        get() = tasks.size

    val productivityPercentage: Float
        get() = if (tasks.isEmpty()) 0f else (todayCompletedCount.toFloat() / tasks.size.toFloat())
}
