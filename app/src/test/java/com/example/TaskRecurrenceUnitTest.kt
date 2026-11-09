package com.example

import com.example.util.TaskRecurrenceHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class TaskRecurrenceUnitTest {

    private val zone = ZoneId.systemDefault()

    @Test
    fun testCalculateNextDueDate_daily() {
        val initialDateTime = LocalDateTime.of(2026, 11, 10, 14, 30)
        val initialMillis = initialDateTime.atZone(zone).toInstant().toEpochMilli()

        val nextMillis = TaskRecurrenceHelper.calculateNextDueDate(initialMillis, "DAILY")
        val nextDateTime = Instant.ofEpochMilli(nextMillis).atZone(zone).toLocalDateTime()

        assertEquals(LocalDateTime.of(2026, 11, 11, 14, 30), nextDateTime)
    }

    @Test
    fun testCalculateNextDueDate_weekly() {
        val initialDateTime = LocalDateTime.of(2026, 11, 10, 9, 0)
        val initialMillis = initialDateTime.atZone(zone).toInstant().toEpochMilli()

        val nextMillis = TaskRecurrenceHelper.calculateNextDueDate(initialMillis, "WEEKLY")
        val nextDateTime = Instant.ofEpochMilli(nextMillis).atZone(zone).toLocalDateTime()

        assertEquals(LocalDateTime.of(2026, 11, 17, 9, 0), nextDateTime)
    }

    @Test
    fun testCalculateNextDueDate_monthly_end_of_month() {
        // Caso de prueba clave: 31 de Enero + 1 mes -> 28 de Febrero
        val initialDateTime = LocalDateTime.of(2026, 1, 31, 10, 0)
        val initialMillis = initialDateTime.atZone(zone).toInstant().toEpochMilli()

        val nextMillis = TaskRecurrenceHelper.calculateNextDueDate(initialMillis, "MONTHLY")
        val nextDateTime = Instant.ofEpochMilli(nextMillis).atZone(zone).toLocalDateTime()

        assertEquals(LocalDateTime.of(2026, 2, 28, 10, 0), nextDateTime)
    }

    @Test
    fun testCalculateNextDueDate_yearly() {
        val initialDateTime = LocalDateTime.of(2026, 7, 28, 8, 0)
        val initialMillis = initialDateTime.atZone(zone).toInstant().toEpochMilli()

        val nextMillis = TaskRecurrenceHelper.calculateNextDueDate(initialMillis, "YEARLY")
        val nextDateTime = Instant.ofEpochMilli(nextMillis).atZone(zone).toLocalDateTime()

        assertEquals(LocalDateTime.of(2027, 7, 28, 8, 0), nextDateTime)
    }

    @Test
    fun testCalculateNextDueDate_none_returns_same() {
        val initialDateTime = LocalDateTime.of(2026, 5, 1, 12, 0)
        val initialMillis = initialDateTime.atZone(zone).toInstant().toEpochMilli()

        val nextMillis = TaskRecurrenceHelper.calculateNextDueDate(initialMillis, "NONE")
        assertEquals(initialMillis, nextMillis)
    }

    @Test
    fun testFormatFriendlyDueDate_today_and_tomorrow() {
        val today = LocalDate.now(zone)
        val todayDateTime = today.atTime(15, 0)
        val todayMillis = todayDateTime.atZone(zone).toInstant().toEpochMilli()

        val formattedToday = TaskRecurrenceHelper.formatFriendlyDueDate(todayMillis)
        assertTrue("Debe contener 'Hoy'", formattedToday.contains("Hoy"))

        val tomorrowDateTime = today.plusDays(1).atTime(10, 30)
        val tomorrowMillis = tomorrowDateTime.atZone(zone).toInstant().toEpochMilli()

        val formattedTomorrow = TaskRecurrenceHelper.formatFriendlyDueDate(tomorrowMillis)
        assertTrue("Debe contener 'Mañana'", formattedTomorrow.contains("Mañana"))
    }
}
