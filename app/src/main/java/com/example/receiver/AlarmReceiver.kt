package com.example.receiver

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.data.database.AppDatabase
import com.example.util.AlarmScheduler
import com.example.util.AlarmSoundPlayer
import com.example.util.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return

        when (action) {
            ACTION_TRIGGER_ALARM -> {
                val alarmId = intent.getLongExtra(EXTRA_ALARM_ID, 0L)
                val alarmTitle = intent.getStringExtra(EXTRA_ALARM_TITLE) ?: "Despertador"
                val soundUri = intent.getStringExtra(EXTRA_ALARM_SOUND_URI) ?: ""
                val vibrate = intent.getBooleanExtra(EXTRA_ALARM_VIBRATE, true)

                // Reproducir sonido de alarma audible y vibrar teléfono (incluso con pantalla bloqueada)
                AlarmSoundPlayer.playAlarm(context, soundUri, vibrate)

                // Mostrar notificación de alta prioridad
                NotificationHelper.showAlarmNotification(
                    context,
                    alarmId,
                    alarmTitle,
                    "¡Hora de levantarse! Revisa tus compromisos en tu agenda digital."
                )
            }

            ACTION_TASK_REMINDER -> {
                val taskId = intent.getLongExtra(EXTRA_TASK_ID, 0L)
                val taskTitle = intent.getStringExtra(EXTRA_TASK_TITLE) ?: "Tarea pendiente"

                // Reproducir tono de notificación y vibración suave para tareas
                AlarmSoundPlayer.playTaskReminder(context)

                NotificationHelper.showTaskReminderNotification(
                    context,
                    taskId,
                    taskTitle,
                    "Tienes una tarea programada en tu agenda personal."
                )
            }

            ACTION_SNOOZE -> {
                // Detener sonido de alarma actual
                AlarmSoundPlayer.stop()

                val alarmId = intent.getLongExtra(EXTRA_ALARM_ID, 0L)
                val soundUri = intent.getStringExtra(EXTRA_ALARM_SOUND_URI) ?: ""
                val vibrate = intent.getBooleanExtra(EXTRA_ALARM_VIBRATE, true)

                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(alarmId.toInt())

                // Posponer 10 minutos
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val snoozeIntent = Intent(context, AlarmReceiver::class.java).apply {
                    this.action = ACTION_TRIGGER_ALARM
                    putExtra(EXTRA_ALARM_ID, alarmId)
                    putExtra(EXTRA_ALARM_TITLE, "Alarma (Pospuesta)")
                    putExtra(EXTRA_ALARM_SOUND_URI, soundUri)
                    putExtra(EXTRA_ALARM_VIBRATE, vibrate)
                }
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    alarmId.toInt(),
                    snoozeIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                val triggerAt = System.currentTimeMillis() + (10 * 60 * 1000L)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
                }
            }

            ACTION_DISMISS -> {
                // Detener sonido y vibración
                AlarmSoundPlayer.stop()

                val alarmId = intent.getLongExtra(EXTRA_ALARM_ID, 0L)
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(alarmId.toInt())
            }

            Intent.ACTION_BOOT_COMPLETED -> {
                // Restaurar todas las alarmas activas al reiniciar dispositivo
                val db = AppDatabase.getDatabase(context)
                CoroutineScope(Dispatchers.IO).launch {
                    val alarms = db.agendaDao().getAllAlarms().first()
                    alarms.filter { it.isEnabled }.forEach { alarm ->
                        AlarmScheduler.scheduleAlarm(context, alarm)
                    }
                }
            }
        }
    }

    companion object {
        const val ACTION_TRIGGER_ALARM = "com.example.agenda.TRIGGER_ALARM"
        const val ACTION_TASK_REMINDER = "com.example.agenda.TASK_REMINDER"
        const val ACTION_SNOOZE = "com.example.agenda.ACTION_SNOOZE"
        const val ACTION_DISMISS = "com.example.agenda.ACTION_DISMISS"

        const val EXTRA_ALARM_ID = "extra_alarm_id"
        const val EXTRA_ALARM_TITLE = "extra_alarm_title"
        const val EXTRA_ALARM_SOUND_URI = "extra_alarm_sound_uri"
        const val EXTRA_ALARM_VIBRATE = "extra_alarm_vibrate"
        const val EXTRA_TASK_ID = "extra_task_id"
        const val EXTRA_TASK_TITLE = "extra_task_title"
    }
}
