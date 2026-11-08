package com.example.data.repository

import com.example.data.dao.AgendaDao
import com.example.data.model.AlarmEntity
import com.example.data.model.EventEntity
import com.example.data.model.LabelEntity
import com.example.data.model.TaskEntity
import com.example.data.provider.PeruHolidaysProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class AgendaRepository(private val dao: AgendaDao) {

    // Streams
    val allLabels: Flow<List<LabelEntity>> = dao.getAllLabels()
    val allTasks: Flow<List<TaskEntity>> = dao.getAllTasks()
    val allEvents: Flow<List<EventEntity>> = dao.getAllEvents()
    val allAlarms: Flow<List<AlarmEntity>> = dao.getAllAlarms()

    // Labels CRUD
    suspend fun insertLabel(label: LabelEntity): Long = dao.insertLabel(label)
    suspend fun updateLabel(label: LabelEntity) = dao.updateLabel(label)
    suspend fun deleteLabel(id: Long) = dao.deleteLabelById(id)

    // Tasks CRUD
    suspend fun insertTask(task: TaskEntity): Long = dao.insertTask(task)
    suspend fun updateTask(task: TaskEntity) = dao.updateTask(task)
    suspend fun setTaskCompleted(id: Long, isCompleted: Boolean) = dao.setTaskCompleted(id, isCompleted)
    suspend fun deleteTask(id: Long) = dao.deleteTaskById(id)

    // Events CRUD
    suspend fun insertEvent(event: EventEntity): Long = dao.insertEvent(event)
    suspend fun updateEvent(event: EventEntity) = dao.updateEvent(event)
    suspend fun deleteEvent(id: Long) = dao.deleteEventById(id)

    // Alarms CRUD
    suspend fun insertAlarm(alarm: AlarmEntity): Long = dao.insertAlarm(alarm)
    suspend fun updateAlarm(alarm: AlarmEntity) = dao.updateAlarm(alarm)
    suspend fun deleteAlarm(id: Long) = dao.deleteAlarmById(id)

    suspend fun toggleAlarm(alarm: AlarmEntity) {
        val updated = alarm.copy(isEnabled = !alarm.isEnabled)
        dao.updateAlarm(updated)
    }

    suspend fun ensurePeruHolidays(year: Int) {
        val existingEvents = dao.getAllEvents().first()
        val hasHolidaysForYear = existingEvents.any { it.isPeruHoliday }
        if (!hasHolidaysForYear) {
            val holidays = PeruHolidaysProvider.getHolidaysForYear(year)
            dao.insertEvents(holidays.map { PeruHolidaysProvider.toEventEntity(it) })
        }
    }
}
