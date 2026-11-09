# 📅 Programación Anticipada de Tareas y Tareas Recurrentes

Este plan detalla la arquitectura, modelo de datos y diseño de experiencia de usuario para permitir programar tareas para cualquier día en el futuro (con selector de fecha y hora), así como configurar tareas periódicas o recurrentes (**Diaria**, **Semanal**, **Mensual**, **Anual** o **Sin repetición**). Al completar una tarea recurrente, el sistema avanzará automáticamente la fecha de vencimiento al siguiente ciclo y la restablecerá en estado *"Por Hacer"* con notificación de confirmación, manteniéndola visible en la fecha seleccionada en el **Calendario** y en el **Panel Diario**.

---

## 🎯 Decisiones Críticas y Preferencias Confirmadas

> [!IMPORTANT]
> - **Tipo de Recurrencia**: Configurable en opciones **Ninguna**, **Diaria**, **Semanal**, **Mensual** y **Anual**.
> - **Comportamiento al Completar**: Al marcar o completar una tarea recurrente, el sistema registra el logro, reproduce el sonido de confirmación, suma los puntos de productividad (XP) y **avanza automáticamente su fecha al siguiente ciclo programado** (reabriendo el ciclo en *"Por Hacer"*). Para tareas no recurrentes, permanece en *"Completada"*.
> - **Visualización en Calendario y Panel**: 
>   1. En el **Calendario**: los días que tengan tareas programadas mostrarán un indicador visual distintivo junto con los eventos, y al seleccionar cualquier día se listarán las tareas programadas para esa fecha específica.
>   2. En el **Panel Principal (Dashboard)**: mostrará las tareas correspondientes a la fecha seleccionada o de la jornada, con acceso a selector de fecha rápido (*Hoy*, *Mañana*, *Esta semana*, etc.).

---

## 1. Experiencia de Usuario y Flujo de Interacción

```
┌────────────────────────────────────────────────────────────────────────┐
│                        NUEVA / EDITAR TAREA                            │
├────────────────────────────────────────────────────────────────────────┤
│ Título: [ Pagar servicios de oficina / Declaración mensual ]          │
│                                                                        │
│ 📅 FECHA DE PROGRAMACIÓN ANTICIPADA                                    │
│ [ 📆 Seleccionar Fecha: 15 de Noviembre, 2026 ] [ ⏰ Hora: 09:30 AM ]  │
│ Botones rápidos: [ Hoy ] [ Mañana ] [ Próx. Lunes ] [ En 1 mes ]      │
│                                                                        │
│ 🔁 REPETICIÓN / RECURRENCIA                                            │
│ [ Ninguna ]  [ 🔄 Diaria ]  [ 📅 Semanal ]  [ 🗓️ Mensual ]  [ 🎂 Anual ]│
│                                                                        │
│ 🎯 Matriz Eisenhower: [ 🔥 Hacer Ya ] [ 💎 Planificar ] ...            │
│ 🏷️ Etiqueta Pastel:    [ Finanzas ]   [ Trabajo ] ...                   │
│ ⏰ Recordatorio:       [ 15 min antes ] [ Exacto ] ...                 │
└────────────────────────────────────────────────────────────────────────┘
```

1. **Creación o Edición de Tarea**:
   - En `CreateTaskDialog`, el usuario puede elegir cualquier fecha futura mediante un selector de fecha amigable (Material DatePicker dialog o selector visual nativo) y hora de vencimiento.
   - Cuenta con atajos rápidos: **Hoy**, **Mañana**, **Próxima Semana** y **En 1 Mes**.
   - Selector visual de recurrencia en píldoras: `NONE` (Sin repetición), `DAILY` (Diaria), `WEEKLY` (Semanal), `MONTHLY` (Mensual), `YEARLY` (Anual).

2. **Visualización y Organización Temporal**:
   - **En el Calendario**: Cada celda mensual/semanal/diaria detecta tanto eventos como tareas programadas. Al tocar un día específico, la agenda del día lista las tareas asignadas a esa fecha exacta con un badge de recurrencia (ej. `🗓️ Mensual`).
   - **En el Dashboard**: Muestra las tareas para la fecha activa (con opción de alternar entre *Hoy* y la fecha seleccionada en el calendario), además de un contador de tareas futuras programadas.

3. **Ciclo de Completado Inteligente**:
   - Al completar una tarea:
     - Si `recurrenceInterval == NONE`: Pasa a `DONE` (Completada).
     - Si es recurrente (`DAILY`, `WEEKLY`, `MONTHLY`, `YEARLY`): Se reproduce el sonido de confirmación, se emite la notificación de logro, y su `dueDateMillis` se proyecta exactamente al siguiente período (+1 día, +1 semana, +1 mes o +1 año manteniendo la hora programada), volviendo al estado `TODO` para el siguiente ciclo.

---

## 2. Modelo de Datos y Arquitectura Técnica

```
┌─────────────────────────┐          ┌───────────────────────────┐
│       TaskEntity        │          │   RecurrenceInterval      │
├─────────────────────────┤          ├───────────────────────────┤
│ id: Long                │          │ NONE    ("No se repite")  │
│ title: String           │          │ DAILY   ("Diario")        │
│ dueDateMillis: Long     │◄─────────┤ WEEKLY  ("Semanal")       │
│ recurrenceInterval: Str │          │ MONTHLY ("Mensual")       │
│ recurrenceEndMillis: L? │          │ YEARLY  ("Anual")         │
│ kanbanStatus: String    │          └───────────────────────────┘
│ isCompleted: Boolean    │
└───────────┬─────────────┘
            │
            ▼
┌─────────────────────────┐
│     AgendaViewModel     │ ───► cycleTaskState(task)
│  - calculateNextDueDate │         └─► Si es recurrente: avanza período y resetea TODO
│  - scheduleTaskReminder │
└─────────────────────────┘
```

### Modificaciones en `TaskEntity.kt`:
```kotlin
enum class TaskRecurrence(val label: String, val icon: String) {
    NONE("No se repite", "⛔"),
    DAILY("Diaria", "🔄"),
    WEEKLY("Semanal", "📅"),
    MONTHLY("Mensual", "🗓️"),
    YEARLY("Anual", "🎂")
}

// En TaskEntity:
val recurrenceInterval: String = "NONE", // NONE, DAILY, WEEKLY, MONTHLY, YEARLY
```

### Cálculo del Siguiente Ciclo:
Uso de `java.time.Instant`, `ZoneId.systemDefault()`, `LocalDateTime`:
- `DAILY`: `currentDateTime.plusDays(1)`
- `WEEKLY`: `currentDateTime.plusWeeks(1)`
- `MONTHLY`: `currentDateTime.plusMonths(1)` (maneja fin de mes automáticamente como 31 de enero -> 28/29 de febrero)
- `YEARLY`: `currentDateTime.plusYears(1)`

---

## 3. Componentes y Pantallas a Actualizar

1. **`TaskEntity.kt` y `AppDatabase.kt`**:
   - Agregar el campo `recurrenceInterval` a `TaskEntity`.
   - Incrementar versión de base de datos a `3` en `AppDatabase` con `fallbackToDestructiveMigration()`.

2. **`CreateTaskDialog.kt`**:
   - Añadir selector de fecha (con fecha editable, diálogo de calendario `DatePickerDialog` de Android o Compose M3) y atajos (*Hoy*, *Mañana*, *En 7 días*, *En 30 días*).
   - Añadir selector de hora precisa.
   - Añadir selector de recurrencia en píldoras atractivas M3 (`No repite`, `Diaria`, `Semanal`, `Mensual`, `Anual`).

3. **`TaskItemCard.kt`**:
   - Mostrar indicador de fecha de vencimiento amigable (ej. *"Hoy a las 4:00 PM"*, *"Mañana a las 10:00 AM"*, *"15 nov a las 9:00 AM"*).
   - Mostrar distintivo de recurrencia si aplica (ej. `🔄 Mensual`).

4. **`AgendaViewModel.kt`**:
   - Función `advanceRecurrentTask(task: TaskEntity)`: calcula la próxima fecha, actualiza la alarma/recordatorio programado y resetea el estado a `TODO`.
   - Reprogramación exacta mediante `AlarmScheduler` con el nuevo timestamp.

5. **`CalendarScreen.kt` & `DashboardScreen.kt`**:
   - En `CalendarScreen`: la cuadrícula del mes mostrará un indicador de puntos o tareas en las fechas que tengan tareas programadas (además de los eventos).
   - En la sección "Agenda del Día", se mostrarán las tareas que vencen en la fecha seleccionada.

6. **Pruebas Unitarias (`AppVersionUnitTest.kt` o `TaskRecurrenceUnitTest.kt`)**:
   - Pruebas para cálculo correcto de fechas siguientes (diaria, semanal, fin de mes mensual y bisiestos anuales).

---

## 4. Verificación y Calidad
- Ejecutar compilación con `compile_applet`.
- Ejecutar suite de pruebas con `gradle :app:testDebugUnitTest`.
- Probar la creación de una tarea para una fecha futura y verificar que aparece en el día correspondiente en el calendario.
- Completar una tarea mensual y verificar que avanza automáticamente al mes siguiente.
