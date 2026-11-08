package com.example.util

import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import androidx.core.content.FileProvider
import com.example.data.model.EventEntity
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object GoogleCalendarHelper {

    fun exportToGoogleCalendarIntent(context: Context, event: EventEntity): Intent {
        return Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, event.title)
            putExtra(CalendarContract.Events.DESCRIPTION, event.description)
            putExtra(CalendarContract.Events.EVENT_LOCATION, event.location)
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.startMillis)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, event.endMillis)
            putExtra(CalendarContract.Events.ALL_DAY, event.isAllDay)
        }
    }

    fun generateIcsFileAndShare(context: Context, events: List<EventEntity>): Intent {
        val dateFormat = SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'", Locale.US)
        val sb = StringBuilder()
        sb.append("BEGIN:VCALENDAR\n")
        sb.append("VERSION:2.0\n")
        sb.append("PRODID:-//Agenda Digital Inteligente//ES\n")
        sb.append("CALSCALE:GREGORIAN\n")

        for (event in events) {
            sb.append("BEGIN:VEVENT\n")
            sb.append("UID:event-${event.id}@agendadigital.com\n")
            sb.append("DTSTAMP:${dateFormat.format(Date())}\n")
            sb.append("DTSTART:${dateFormat.format(Date(event.startMillis))}\n")
            sb.append("DTEND:${dateFormat.format(Date(event.endMillis))}\n")
            sb.append("SUMMARY:${event.title.replace("\n", " ")}\n")
            sb.append("DESCRIPTION:${event.description.replace("\n", " ")}\n")
            if (event.location.isNotEmpty()) {
                sb.append("LOCATION:${event.location.replace("\n", " ")}\n")
            }
            sb.append("STATUS:CONFIRMED\n")
            sb.append("END:VEVENT\n")
        }
        sb.append("END:VCALENDAR\n")

        val file = File(context.cacheDir, "agenda_digital_eventos.ics")
        file.writeText(sb.toString())

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        return Intent(Intent.ACTION_SEND).apply {
            type = "text/calendar"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Eventos de Agenda Digital para Google Calendar")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }
}
