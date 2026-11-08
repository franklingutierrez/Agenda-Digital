package com.example.data.provider

import com.example.data.model.EventEntity
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

data class PeruHoliday(
    val date: LocalDate,
    val name: String,
    val description: String,
    val isNational: Boolean = true
)

object PeruHolidaysProvider {

    fun getHolidaysForYear(year: Int): List<PeruHoliday> {
        val list = mutableListOf<PeruHoliday>()

        // Feriados fijos oficiales según normativa peruana
        list.add(PeruHoliday(LocalDate.of(year, 1, 1), "Año Nuevo", "Inicio del año calendario oficial"))
        list.add(PeruHoliday(LocalDate.of(year, 5, 1), "Día del Trabajo", "Día Internacional de los Trabajadores"))
        list.add(PeruHoliday(LocalDate.of(year, 6, 7), "Día de la Bandera y Batalla de Arica", "Homenaje a los defensores del Morro de Arica y Francisco Bolognesi (Ley 31701)"))
        list.add(PeruHoliday(LocalDate.of(year, 6, 29), "San Pedro y San Pablo", "Festividad religiosa y Día del Pescador"))
        list.add(PeruHoliday(LocalDate.of(year, 7, 23), "Día de la Fuerza Aérea del Perú", "Héroe Nacional Capitán FAP José Abelardo Quiñones (Ley 31822)"))
        list.add(PeruHoliday(LocalDate.of(year, 7, 28), "Fiestas Patrias - Proclamación de la Independencia", "Conmemoración de la independencia del Perú por Don José de San Martín (1821)"))
        list.add(PeruHoliday(LocalDate.of(year, 7, 29), "Fiestas Patrias - Gran Parada Militar", "Homenaje a las Fuerzas Armadas y Policía Nacional del Perú"))
        list.add(PeruHoliday(LocalDate.of(year, 8, 6), "Batalla de Junín", "Victoria patriota en la Pampa de Junín de 1824 (Ley 31530)"))
        list.add(PeruHoliday(LocalDate.of(year, 8, 30), "Santa Rosa de Lima", "Patrona de América, las Filipinas y la Policía Nacional"))
        list.add(PeruHoliday(LocalDate.of(year, 10, 8), "Combate de Angamos", "Homenaje al Almirante Miguel Grau Seminario y la Marina de Guerra"))
        list.add(PeruHoliday(LocalDate.of(year, 11, 1), "Día de Todos los Santos", "Conmemoración tradicional peruana a los seres queridos"))
        list.add(PeruHoliday(LocalDate.of(year, 12, 8), "Inmaculada Concepción", "Festividad tradicional católica"))
        list.add(PeruHoliday(LocalDate.of(year, 12, 9), "Batalla de Ayacucho", "Consolidación de la Independencia Americana (Ley 31381)"))
        list.add(PeruHoliday(LocalDate.of(year, 12, 25), "Navidad del Señor", "Celebración del nacimiento de Jesús"))

        // Feriados movibles de Semana Santa calculados por Algoritmo de Gauss / Anonymous Gregorian
        val easter = calculateEaster(year)
        val juevesSanto = easter.minusDays(3)
        val viernesSanto = easter.minusDays(2)

        list.add(PeruHoliday(juevesSanto, "Jueves Santo", "Semana Santa - Institución de la Eucaristía"))
        list.add(PeruHoliday(viernesSanto, "Viernes Santo", "Semana Santa - Pasión y Muerte de Jesús"))

        return list.sortedBy { it.date }
    }

    fun isHoliday(date: LocalDate): PeruHoliday? {
        val holidays = getHolidaysForYear(date.year)
        return holidays.find { it.date == date }
    }

    fun getNextUpcomingHoliday(fromDate: LocalDate = LocalDate.now()): PeruHoliday {
        val thisYearHolidays = getHolidaysForYear(fromDate.year)
        val nextInThisYear = thisYearHolidays.firstOrNull { !it.date.isBefore(fromDate) }
        if (nextInThisYear != null) return nextInThisYear

        val nextYearHolidays = getHolidaysForYear(fromDate.year + 1)
        return nextYearHolidays.first()
    }

    fun toEventEntity(holiday: PeruHoliday): EventEntity {
        val zone = ZoneId.systemDefault()
        val startMillis = holiday.date.atStartOfDay(zone).toInstant().toEpochMilli()
        val endMillis = holiday.date.atTime(23, 59, 59).atZone(zone).toInstant().toEpochMilli()

        return EventEntity(
            title = "🇵🇪 " + holiday.name,
            description = holiday.description,
            startMillis = startMillis,
            endMillis = endMillis,
            isAllDay = true,
            isPeruHoliday = true,
            location = "Perú"
        )
    }

    /**
     * Algoritmo de Meeus/Jones/Butcher para el cálculo de la Pascua Gregoriana
     */
    private fun calculateEaster(year: Int): LocalDate {
        val a = year % 19
        val b = year / 100
        val c = year % 100
        val d = b / 4
        val e = b % 4
        val f = (b + 8) / 25
        val g = (b - f + 1) / 3
        val h = (19 * a + b - d - g + 15) % 30
        val i = c / 4
        val k = c % 4
        val l = (32 + 2 * e + 2 * i - h - k) % 7
        val m = (a + 11 * h + 22 * l) / 451
        val month = (h + l - 7 * m + 114) / 31
        val day = ((h + l - 7 * m + 114) % 31) + 1
        return LocalDate.of(year, month, day)
    }
}
