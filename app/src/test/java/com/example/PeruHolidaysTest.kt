package com.example

import com.example.data.provider.PeruHolidaysProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class PeruHolidaysTest {

    @Test
    fun testPeruHolidaysFor2026() {
        val holidays = PeruHolidaysProvider.getHolidaysForYear(2026)

        // Comprobar que contiene los feriados nacionales oficiales de Perú
        assertTrue("Debe contener Fiestas Patrias 28 de Julio", holidays.any { it.date == LocalDate.of(2026, 7, 28) })
        assertTrue("Debe contener Fiestas Patrias 29 de Julio", holidays.any { it.date == LocalDate.of(2026, 7, 29) })
        assertTrue("Debe contener Santa Rosa de Lima 30 de Agosto", holidays.any { it.date == LocalDate.of(2026, 8, 30) })
        assertTrue("Debe contener Combate de Angamos 8 de Octubre", holidays.any { it.date == LocalDate.of(2026, 10, 8) })
        assertTrue("Debe contener Batalla de Junín 6 de Agosto", holidays.any { it.date == LocalDate.of(2026, 8, 6) })
        assertTrue("Debe contener Batalla de Ayacucho 9 de Diciembre", holidays.any { it.date == LocalDate.of(2026, 12, 9) })
        assertTrue("Debe contener Día de la Bandera 7 de Junio", holidays.any { it.date == LocalDate.of(2026, 6, 7) })

        // Feriados de Semana Santa (Jueves y Viernes Santo)
        val juevesSanto = holidays.find { it.name == "Jueves Santo" }
        val viernesSanto = holidays.find { it.name == "Viernes Santo" }
        assertNotNull("Jueves Santo debe estar calculado", juevesSanto)
        assertNotNull("Viernes Santo debe estar calculado", viernesSanto)
        assertEquals(1, viernesSanto!!.date.dayOfMonth - juevesSanto!!.date.dayOfMonth)
    }

    @Test
    fun testUpcomingHoliday() {
        val fromDate = LocalDate.of(2026, 7, 1)
        val next = PeruHolidaysProvider.getNextUpcomingHoliday(fromDate)
        assertEquals("Día de la Fuerza Aérea del Perú", next.name)
        assertEquals(LocalDate.of(2026, 7, 23), next.date)
    }
}
