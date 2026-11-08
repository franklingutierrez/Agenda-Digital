# Agenda Digital Inteligente: Plan de Implementación

Una aplicación integral de productividad y organización personal en Jetpack Compose, con panel diario interactivo, gestión de eventos y tareas con etiquetas de colores pastel, despertador integrado, notificaciones inteligentes, feriados oficiales de Perú y compatibilidad multiplataforma y adaptativa.

### User Review & Critical Decisions

> [!IMPORTANT]
> **Decisiones de Arquitectura y Configuración**:
> - **Persistencia y Base de Datos**: Implementaremos **Room Database** con soporte reactivo mediante Kotlin Coroutines `Flow`, permitiendo almacenamiento local seguro, inmediato y confiable con exportación/importación de respaldo y sincronización con el calendario del dispositivo (`CalendarContract` / Google Calendar).
> - **Etiquetas Personalizables de Colores**: Sistema dinámico de etiquetas (ej. *Trabajo*, *Salud*, *Personal*, *Estudio*, *Finanzas*) con una paleta de tonos pasteles personalizable y selector de iconos, visibles en tarjetas de eventos, tareas y filtros rápidos en el panel.
> - **Feriados Oficiales de Perú**: Catálogo precargado de feriados nacionales peruanos (Ley 31530 y decretos vigentes: Año Nuevo, Semana Santa, Día de la Bandera, Fiestas Patrias, Santa Rosa de Lima, Batalla de Ayacucho, Navidad, etc.) con cálculo automático y distintivo visual en el calendario.
> - **Despertador y Notificaciones**: Servicio de alarmas programadas con `AlarmManager`, notificaciones push locales con canales específicos de recordatorio y pantalla de despertador con opciones de posponer (*Snooze*) y descartar con resumen matutino.

---

### 1. Overview & Core Concept

- **Qué hace**: Proporciona un centro de control diario donde el usuario puede planificar su jornada, gestionar eventos en vistas mensual, semanal y diaria, ejecutar y marcar tareas con subtareas y prioridades, configurar alarmas despertadoras y recibir alertas oportunas, todo con estética pastel y feriados peruanos integrados.
- **Público Objetivo**: Profesionales, estudiantes y cualquier persona que busque organizar su tiempo con claridad mental, seguimiento de productividad y control sobre sus compromisos.
- **Valor Principal**: Combina en una única interfaz cohesiva la agenda de eventos, la lista de tareas procesable de alta productividad, el despertador matutino y la sincronización con el calendario oficial peruano, evitando la fragmentación entre múltiples aplicaciones.

---

### 2. User Experience & Visual Design

#### Flujos de Usuario Clave
1. **Dashboard Diario (Panel Intuitivo)**:
   - Saludo contextual según la hora del día (Buenos días / tardes / noches).
   - Próximo evento destacado con contador regresivo.
   - Barra de progreso diario de tareas ("4 de 6 tareas completadas - 67%").
   - Fila de filtros rápidos por etiqueta (Todas, Trabajo, Salud, Personal).
   - Acceso rápido a crear evento, tarea o alarma.
2. **Calendario y Feriados de Perú**:
   - Vistas interactiva mensual y semanal con indicadores de puntos de colores según las etiquetas del evento.
   - Destaque especial en días de feriado nacional peruano con badge patrio/oficial e información conmemorativa.
   - Integración para sincronizar y exportar eventos a Google Calendar / Calendario del sistema.
3. **Gestor de Tareas de Alta Productividad**:
   - Checkbox interactivo para marcar tareas al instante con micro-animación de tachado y confetti/retroalimentación.
   - Nivel de prioridad (Alta, Media, Baja) con indicadores visuales sutiles.
   - Filtro por estado (Pendientes, Completadas) y por fecha de vencimiento.
4. **Despertador y Recordatorios Inteligentes**:
   - Lista de alarmas con interruptor activo/inactivo, selector de hora en formato 12/24h, días repetitivos y tono.
   - Notificación de recordatorio 15 o 30 minutos antes de citas importantes.
   - Pantalla completa de alarma matutina que muestra el clima y el resumen de compromisos del día al apagarse.
5. **Gestor de Etiquetas**:
   - Creación y edición de categorías personalizadas con selector de paleta pastel y nombre personalizado.

#### Identidad Visual y Tema (Paleta Pastel & Modo Oscuro)
- **Modo Claro (Pastel Sereno)**:
  - Fondo: Blanco perla suave (`#FAF9F6`) y lavanda muy tenue (`#F4F3FA`).
  - Primario: Lavanda profundo suave (`#7C6DAF`).
  - Acentos pasteles para etiquetas:
    * Trabajo / Estudio: Azul cielo pastel (`#93C5FD` / texto `#1E40AF`)
    * Salud / Bienestar: Menta pastel (`#86EFAC` / texto `#166534`)
    * Personal / Hogar: Durazno pastel (`#FDBA74` / texto `#9A3412`)
    * Importante / Urgente: Rosa coral pastel (`#FCA5A5` / texto `#991B1B`)
    * Creativo / Ocio: Lavanda pastel (`#C4B5FD` / texto `#5B21B6`)
- **Modo Oscuro (Dark Pastel / Slate Elegante)**:
  - Fondo: Grafito suave nocturno (`#12131A`).
  - Superficies de tarjeta: Azul grisáceo oscuro (`#1E202B`).
  - Colores de acento ajustados en luminancia para mantener legibilidad sin fatiga visual.
  - Soporte automático según el sistema (`isSystemInDarkTheme()`) con alternancia manual en ajustes.
- **Tipografía y Jerarquía**:
  - Títulos limpios y modernos con peso semi-bold.
  - Números legibles en el calendario y reloj despertador.
  - Tamaños accesibles respetando escala de fuente del sistema (`sp`).

---

### 3. Key Product Decisions & Trade-Offs

- **Decisión 1: Integración con Calendarios Externos (Google Calendar)**
  - *Enfoque*: Uso de los proveedores del sistema de Android (`CalendarContract.Events`) y archivo universal iCalendar (.ics) para exportación/importación bidireccional.
  - *Razón*: Permite que los eventos se sincronicen directamente con la cuenta de Google vinculada en el dispositivo sin exigir configuración de backend externa ni dependencias frágiles.
  - *Alternativas consideradas*: API directa REST de Google Calendar (requiere credenciales complejas de consola GCP por usuario).
- **Decisión 2: Sistema de Alarmas y Notificaciones**
  - *Enfoque*: Implementación de `AlarmManager` con `BroadcastReceiver` (`AlarmReceiver`) y canales de notificación de alta prioridad para garantizar que el despertador suene en el momento exacto.
  - *Razón*: Es el mecanismo nativo estándar de Android para despertadores y alertas confiables en segundo plano.
- **Decisión 3: Adaptabilidad de Pantalla (Móvil, Tablet, Escritorio)**
  - *Enfoque*: Diseño responsivo con `BoxWithConstraints` y diseño canónico M3: `NavigationBar` inferior en pantallas compactas (teléfonos) y `NavigationRail` lateral en pantallas medianas/expandidas (tablets, emulador en navegador o modo escritorio).
  - *Razón*: Cumple con las directrices de diseño adaptable y asegura que la aplicación se vea impecable tanto en el teléfono como en la vista web de AI Studio.

---

### 4. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────────────────────┐
│                        Jetpack Compose UI Layer                        │
├────────────────────────────────────────────────────────────────────────┤
│  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────────┐  │
│  │  DashboardScreen │  │  CalendarScreen  │  │     TasksScreen      │  │
│  │ (Resumen/Filtros)│  │ (Mes/Semana/Perú)│  │  (Check/Prioridad)   │  │
│  └────────┬─────────┘  └────────┬─────────┘  └──────────┬───────────┘  │
│           │                     │                       │              │
│  ┌────────┴─────────┐  ┌────────┴─────────┐  ┌──────────┴───────────┐  │
│  │   AlarmsScreen   │  │   LabelsSheet    │  │    SettingsSheet     │  │
│  │  (Despertador)   │  │ (Gestión Colores)│  │ (Modo Oscuro/Sync)   │  │
│  └────────┬─────────┘  └────────┬─────────┘  └──────────┬───────────┘  │
└───────────┼─────────────────────┼───────────────────────┼──────────────┘
            ▼                     ▼                       ▼
┌────────────────────────────────────────────────────────────────────────┐
│                        AgendaViewModel & State                         │
│  - uiState: StateFlow<AgendaUiState>                                   │
│  - Métodos: toggleTask, addEvent, saveLabel, toggleAlarm, syncCalendar │
└───────────────────────────────────┬────────────────────────────────────┘
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                           Repository Layer                             │
│  - AgendaRepository (Eventos, Tareas, Etiquetas, Alarmas)              │
│  - PeruHolidaysProvider (Feriados oficiales peruanos calculados)       │
│  - GoogleCalendarSyncManager (Intent/Contract sincronización)          │
└───────────────────────────────────┬────────────────────────────────────┘
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                    Room Database & Android Framework                   │
│  ┌──────────────────────┐  ┌──────────────────┐  ┌──────────────────┐  │
│  │      AgendaDao       │  │   AlarmManager   │  │   Notification   │  │
│  │ (Events/Tasks/Labels)│  │ (BroadcastRecv)  │  │     Manager      │  │
│  └──────────────────────┘  └──────────────────┘  └──────────────────┘  │
└────────────────────────────────────────────────────────────────────────┘
```

#### Modelo de Datos Principal
- **LabelEntity**: `id`, `name`, `colorHex`, `iconName`. (Predefinidas: Trabajo, Salud, Personal, Estudio, etc.).
- **TaskEntity**: `id`, `title`, `description`, `labelId`, `dueDate`, `priority`, `isCompleted`, `reminderMinutesBefore`.
- **EventEntity**: `id`, `title`, `description`, `labelId`, `startTime`, `endTime`, `location`, `isAllDay`, `isPeruHoliday`.
- **AlarmEntity**: `id`, `hour`, `minute`, `label`, `isEnabled`, `repeatDays` (Lunes a Domingo), `vibrate`.
- **PeruHoliday**: `date`, `name`, `description`, `lawReference`.

#### Notificaciones y Permisos
- Permiso `POST_NOTIFICATIONS` (Android 13+) y `SCHEDULE_EXACT_ALARM`.
- Canal de notificación: `recordatorios_canal` (Recordatorios de tareas y eventos) y `alarmas_canal` (Despertador con sonido y vibración continua).
