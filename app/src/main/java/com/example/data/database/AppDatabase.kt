package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.AgendaDao
import com.example.data.model.AlarmEntity
import com.example.data.model.EventEntity
import com.example.data.model.LabelEntity
import com.example.data.model.TaskEntity
import com.example.data.model.TaskPriority
import com.example.data.provider.PeruHolidaysProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

@Database(
    entities = [LabelEntity::class, TaskEntity::class, EventEntity::class, AlarmEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun agendaDao(): AgendaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "agenda_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val context: Context
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database.agendaDao())
                    }
                }
            }

            private suspend fun populateInitialData(dao: AgendaDao) {
                // 1. Etiquetas Pasteles Personalizables por Defecto
                val defaultLabels = listOf(
                    LabelEntity(id = 1, name = "Trabajo", colorHex = "#93C5FD", iconName = "Work"),
                    LabelEntity(id = 2, name = "Salud", colorHex = "#86EFAC", iconName = "FitnessCenter"),
                    LabelEntity(id = 3, name = "Personal", colorHex = "#FDBA74", iconName = "Person"),
                    LabelEntity(id = 4, name = "Estudio", colorHex = "#C4B5FD", iconName = "School"),
                    LabelEntity(id = 5, name = "Finanzas", colorHex = "#FEF08A", iconName = "Payments")
                )
                dao.insertLabels(defaultLabels)

                val now = System.currentTimeMillis()
                val today = LocalDate.now()

                // 2. Tareas de demostración con Matriz de Eisenhower y Kanban
                val defaultTasks = listOf(
                    TaskEntity(
                        title = "Entregar reporte mensual de desempeño",
                        description = "Cierre de métricas clave y objetivos logrados",
                        labelId = 1,
                        dueDateMillis = now + (2 * 3600 * 1000),
                        priority = TaskPriority.HIGH,
                        isCompleted = false,
                        kanbanStatus = "IN_PROGRESS",
                        eisenhowerQuadrant = "DO_FIRST",
                        reminderMinutesBefore = 15
                    ),
                    TaskEntity(
                        title = "Planificar estrategia del próximo trimestre",
                        description = "Definir metas de alto impacto y presupuesto",
                        labelId = 1,
                        dueDateMillis = now + (24 * 3600 * 1000),
                        priority = TaskPriority.HIGH,
                        isCompleted = false,
                        kanbanStatus = "TODO",
                        eisenhowerQuadrant = "SCHEDULE",
                        reminderMinutesBefore = 30
                    ),
                    TaskEntity(
                        title = "Responder correos de coordinación logística",
                        description = "Confirmar reservas y pedidos de oficina",
                        labelId = 3,
                        dueDateMillis = now + (4 * 3600 * 1000),
                        priority = TaskPriority.MEDIUM,
                        isCompleted = false,
                        kanbanStatus = "TODO",
                        eisenhowerQuadrant = "DELEGATE",
                        reminderMinutesBefore = 15
                    ),
                    TaskEntity(
                        title = "Revisar agenda y planificar metas del día",
                        description = "Organizar prioridades en el panel diario",
                        labelId = 1,
                        dueDateMillis = now - (1 * 3600 * 1000),
                        priority = TaskPriority.HIGH,
                        isCompleted = true,
                        kanbanStatus = "DONE",
                        eisenhowerQuadrant = "DO_FIRST",
                        reminderMinutesBefore = 15
                    ),
                    TaskEntity(
                        title = "Organizar archivos temporales y descargas",
                        description = "Limpieza periódica no urgente",
                        labelId = 4,
                        dueDateMillis = now + (48 * 3600 * 1000),
                        priority = TaskPriority.LOW,
                        isCompleted = false,
                        kanbanStatus = "TODO",
                        eisenhowerQuadrant = "ELIMINATE",
                        reminderMinutesBefore = 0
                    )
                )
                dao.insertTasks(defaultTasks)

                // 3. Feriados Oficiales de Perú para el año actual
                val peruHolidays = PeruHolidaysProvider.getHolidaysForYear(today.year)
                val holidayEvents = peruHolidays.map { PeruHolidaysProvider.toEventEntity(it) }
                dao.insertEvents(holidayEvents)

                // 4. Eventos de la semana
                val sampleEvents = listOf(
                    EventEntity(
                        title = "Planificación Semanal de Estrategia",
                        description = "Revisión de metas y compromisos clave",
                        labelId = 1,
                        startMillis = now + (1 * 3600 * 1000),
                        endMillis = now + (2 * 3600 * 1000),
                        location = "Sala de reuniones / Virtual",
                        isAllDay = false,
                        isPeruHoliday = false
                    ),
                    EventEntity(
                        title = "Cita médica preventiva",
                        description = "Control general anual de salud",
                        labelId = 2,
                        startMillis = now + (24 * 3600 * 1000),
                        endMillis = now + (25 * 3600 * 1000),
                        location = "Clínica San Borja, Lima",
                        isAllDay = false,
                        isPeruHoliday = false
                    )
                )
                dao.insertEvents(sampleEvents)

                // 5. Alarma despertadora integrada predeterminada
                val defaultAlarms = listOf(
                    AlarmEntity(
                        hour = 6,
                        minute = 30,
                        label = "Despertador Matutino",
                        isEnabled = true,
                        repeatDays = "2,3,4,5,6", // Lunes a Viernes
                        vibrate = true,
                        soundName = "Tono Predeterminado"
                    ),
                    AlarmEntity(
                        hour = 8,
                        minute = 0,
                        label = "Despertador Fin de Semana",
                        isEnabled = false,
                        repeatDays = "1,7", // Sábado y Domingo
                        vibrate = true,
                        soundName = "Tono Predeterminado"
                    )
                )
                dao.insertAlarms(defaultAlarms)
            }
        }
    }
}
