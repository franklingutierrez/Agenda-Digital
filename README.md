# 📅 Agenda Digital Inteligente (Perú) 🇵🇪

Una moderna aplicación Android desarrollada en **Kotlin** y **Jetpack Compose** bajo los lineamientos de **Material Design 3**, diseñada para la máxima productividad personal y profesional con metodologías ágiles, recordatorios inteligentes, despertador con sonido/vibración y sincronización oficial de feriados nacionales peruanos.

---

## ✨ Características Principales

### 1. 🎯 Metodologías Productivas
- **Tablero Kanban**: Flujo ágil estilo Trello organizado en tres estados (*Por Hacer*, *En Progreso* y *Completada*) con avance rápido y reanudación interactiva.
- **Matriz de Eisenhower**: Priorización estratégica en 4 cuadrantes (*Hacer Ya*, *Planificar*, *Delegar* y *Postergar*) con colores pasteles adaptables a modo claro y oscuro.
- **Vista de Lista Diaria**: Vista clásica y limpia de compromisos.
- **Gamificación y Superación Personal**: Contador de puntos de productividad (XP) y niveles (*Maestro del Tiempo*, *Estratega Enfocado*, etc.) con consejos de productividad en cada jornada.

### 2. 🇵🇪 Calendario Oficial de Feriados de Perú
- **Cálculo Automático y Autónomo**: Motor nativo `PeruHolidaysProvider` que calcula automáticamente los 16 feriados oficiales según las leyes peruanas (incluyendo las leyes *Ley 31701 Batalla de Arica*, *Ley 31822 Fuerza Aérea*, *Ley 31530 Batalla de Junín*, *Ley 31381 Batalla de Ayacucho*).
- **Semana Santa Dinámica**: Algoritmo matemático astronómico de *Meeus/Jones/Butcher* para determinar con exactitud Jueves y Viernes Santo en cualquier año.
- **Distintivo en Calendario**: Días feriados destacados con el ícono patrio 🇵🇪 y banner de cuenta regresiva en el panel principal.

### 3. ⏰ Despertador Inteligente y Recordatorios con Sonido
- Alarma con reproducción continua (`USAGE_ALARM`), patrón de vibración, WakeLock y compatibilidad con timbres MP3 personalizados.
- Selector de hora híbrido: flechas visuales intuitivas, ingreso manual con teclado numérico y píldoras rápidas (06:00, 06:30, 07:00, 07:30, 08:00).
- Recordatorios inteligentes previos para tareas y eventos (a la hora exacta, 15 min, 30 min, 1 hora antes) con aviso audible y notificación push.

### 4. 🏷️ Sistema de Etiquetas Pasteles Personalizables
- **16 Colores Pasteles Armónicos**: Calculados dinámicamente con `getAdaptiveBadgeColors()` para garantizar legibilidad y alto contraste tanto en tema claro como oscuro.
- **17 Iconos Contextuales**: Trabajo, Salud, Personal, Estudio, Finanzas, Hogar, Compras, Viajes, Comida, Arte, Lectura, Música, Computación/Código, Favoritos y Prioridad Alta.

### 5. 🔄 Actualizaciones Automáticas (OTA vía GitHub Releases)
- La aplicación cuenta con un módulo integrado (`AppUpdateManager`) que consulta automáticamente la API de GitHub al abrir la app.
- Cuando publicas una nueva versión en GitHub, los usuarios reciben una notificación con el cuadro de diálogo para descargar e instalar el APK actualizado directamente con un solo toque.

### 6. 📅 Integración con Google Calendar
- Sincronización mediante el contrato de calendario de Android y generación de archivos estándar `.ics` compartibles con Google Calendar, Outlook y Apple Calendar.

### 7. 📱 Interfaz Adaptativa (Móvil, Tablet y Escritorio)
- Soporte para **Window Size Classes**: usa `NavigationBar` en pantallas compactas y se transforma automáticamente en `NavigationRail` lateral en tablets o emuladores de escritorio.
- Modo oscuro automático según el sistema o conmutable manualmente desde la cabecera del panel.

---

## 🚀 Cómo Subir y Publicar la App en GitHub

### Paso 1: Inicializar el repositorio Git local y subir el código
En tu terminal local o entorno de desarrollo:

```bash
# 1. Inicializar git si no lo tienes
git init

# 2. Agregar todos los archivos
git add .

# 3. Crear el primer commit
git commit -m "feat: versión inicial de Agenda Digital Inteligente Perú v1.0"

# 4. Vincular con tu repositorio de GitHub (reemplaza con tu usuario)
git branch -M main
git remote add origin https://github.com/dragontec691/agenda-digital-peru.git

# 5. Subir a GitHub
git push -u origin main
```

---

## 📦 Sistema de Notificación y Actualización Automática a Usuarios

El sistema funciona con **GitHub Releases** y **GitHub Actions**:

### ¿Cómo enviar una actualización automática a las personas que instalaron la app?

1. **Incrementa la versión en `app/build.gradle.kts`**:
   ```kotlin
   versionCode = 2
   versionName = "1.1"
   ```

2. **Crea un Tag y súbelo a GitHub**:
   ```bash
   git add app/build.gradle.kts
   git commit -m "release: versión 1.1 con nuevas mejoras"
   git tag v1.1
   git push origin main --tags
   ```

3. **Compilación y Entrega Automática**:
   - El flujo de trabajo en `.github/workflows/release.yml` compilará el archivo APK automáticamente y creará el Release oficial en GitHub.
   - Tan pronto como los usuarios abran la aplicación, el `AppUpdateManager` detectará la versión `1.1` mediante la API de GitHub y desplegará en pantalla el cuadro **"¡Nueva Actualización! Versión 1.1 disponible"** con las notas de la versión y el botón **"Actualizar APK"** para instalar la actualización al instante.

---

## 🛠️ Requisitos de Compilación
- **Android Studio Ladybug / Meerkat** o superior.
- **JDK 17**.
- **Android SDK Min**: API 24 (Android 7.0 Nougat).
- **Android SDK Target**: API 36.
- **Room Database v2** con SQLite nativo.
- **Jetpack Compose + Material 3**.

---

## 📄 Licencia
Proyecto distribuido bajo la licencia MIT. Desarrollado con ❤️ para impulsar la productividad y organización.
