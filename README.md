# 📅 Agenda Digital Inteligente (Perú) 🇵🇪

Una moderna aplicación Android desarrollada en **Kotlin** y **Jetpack Compose** bajo los lineamientos de **Material Design 3**, diseñada para la máxima productividad personal y profesional con metodologías ágiles (**Kanban** y **Matriz de Eisenhower**), recordatorios inteligentes con sonido, despertador y sincronización oficial de feriados nacionales peruanos.

- **Repositorio Oficial**: [`franklingutierrez/Agenda-Digital`](https://github.com/franklingutierrez/Agenda-Digital)
- **Application ID**: `com.aistudio.agendadigital.vpxtzq`
- **Versión Inicial**: `1.0` (versionCode `1`)

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
- **Distintivo en Calendario**: Días feriados destacados con el distintivo patrio 🇵🇪 y banner con cuenta regresiva en el panel principal.

### 3. ⏰ Despertador Inteligente y Recordatorios con Sonido
- Alarma con reproducción continua (`USAGE_ALARM`), patrón de vibración, WakeLock y compatibilidad con timbres MP3 personalizados.
- Selector de hora híbrido: flechas visuales intuitivas, ingreso manual con teclado numérico y píldoras rápidas (06:00, 06:30, 07:00, 07:30, 08:00).
- Recordatorios inteligentes previos para tareas y eventos (a la hora exacta, 15 min, 30 min, 1 hora antes) con aviso audible y notificación push.

### 4. 🏷️ Sistema de Etiquetas Pasteles Personalizables
- **16 Colores Pasteles Armónicos**: Calculados dinámicamente con `getAdaptiveBadgeColors()` para garantizar legibilidad y alto contraste tanto en tema claro como oscuro.
- **17 Iconos Contextuales**: Trabajo, Salud, Personal, Estudio, Finanzas, Hogar, Compras, Viajes, Comida, Arte, Lectura, Música, Computación/Código, Favoritos y Prioridad Alta.

### 5. 🔄 Actualizaciones Automáticas (OTA vía GitHub Releases)
- La aplicación cuenta con un módulo integrado (`AppUpdateManager`) que consulta la API de GitHub del repositorio `franklingutierrez/Agenda-Digital`.
- **Aviso Exclusivo para Versiones Superiores**: La app compara numéricamente las versiones semánticas (por ejemplo `1.10` es reconocido correctamente como superior a `1.9`). Si la versión es igual, inferior o el tag es inválido, no se muestra ningún aviso.
- **Verificación de Asset APK**: Solo notifica cuando el Release contiene un APK descargable proveniente del repositorio legítimo.
- **Descarga e Instalación Segura en Android**: La descarga se realiza de forma estándar y transparente a través del navegador web del dispositivo. Por razones de seguridad del sistema operativo Android, **no existe instalación silenciosa ni ejecución en segundo plano con la app cerrada**: Android solicitará siempre la confirmación del usuario para permitir la instalación de fuentes desconocidas/actualizar la app.

---

## 🔐 Configuración de Secrets en GitHub (Obligatorio para Release)

Para que el flujo de compilación y publicación automática (`release.yml`) en GitHub Actions pueda firmar el APK de producción, debes configurar los siguientes secretos en tu repositorio de GitHub (**Settings > Secrets and variables > Actions > Repository secrets**):

| Nombre del Secret | Descripción |
| :--- | :--- |
| `ANDROID_KEYSTORE_BASE64` | Archivo Keystore de subida (`.jks`) codificado en Base64. |
| `STORE_PASSWORD` | Contraseña del almacén de claves (keystore). |
| `KEY_PASSWORD` | Contraseña de la clave privada (`upload`). |

> ⚠️ **Importante**:
> 1. Para que las personas que instalen la app puedan actualizarla sobre la versión anterior sin errores de *"Conflicto de firma / No se pudo instalar el paquete"*, **todas las versiones futuras deben firmarse exactamente con la misma clave y el mismo Keystore**.
> 2. Si alguno de estos tres secretos no está configurado, el workflow fallará de forma temprana y controlada con un mensaje claro, evitando generar releases rotos o no firmados.

---

## 🚀 Flujo de Trabajo desde AI Studio

Para desarrollar y publicar nuevas versiones usando AI Studio y GitHub:

### Paso 1: Realizar cambios en AI Studio
- Modifica el código o solicita mejoras al asistente de AI Studio.

### Paso 2: Incrementar la versión en `app/build.gradle.kts`
Cuando decidas publicar una nueva versión para los usuarios:
```kotlin
android {
  defaultConfig {
    versionCode = 2        // Incrementar en 1 cada vez
    versionName = "1.1"    // Incrementar versión semántica (ej. 1.1, 1.2, 2.0)
  }
}
```

### Paso 3: Sincronizar con GitHub desde AI Studio
- En el menú superior de AI Studio, selecciona la opción para sincronizar / hacer push de tus cambios al repositorio conectado `franklingutierrez/Agenda-Digital` en la rama `main`.

### Paso 4: Crear el Tag de Release en GitHub
Una vez que el commit esté en GitHub:
1. En GitHub, ve a **Releases > Draft a new release** (o vía terminal con `git tag v1.1 && git push origin v1.1`).
2. El tag **debe coincidir exactamente con el `versionName`** precedido de una `v` (ejemplo: para `versionName = "1.1"` el tag debe ser `v1.1`).
3. El workflow `release.yml` se disparará automáticamente:
   - Descarga Android SDK 36.1 y Gradle 9.3.1.
   - Valida la presencia de los Secrets requeridos.
   - Materializa el Keystore en un archivo temporal seguro en el runner.
   - Compila `:app:assembleRelease`.
   - Verifica la firma digital con `apksigner verify`.
   - Publica el Release en GitHub con el archivo `app-release.apk` adjunto.

---

## 🛠️ Exportación de APK y Claves en AI Studio

- **Generación de APK en AI Studio**: AI Studio cuenta con la opción de generar APK / AAB para pruebas e instalación directa desde su menú de exportación y configuración.
- **Claves de Firma**: Las claves de firma para publicación de producción en tiendas o en CI/CD externo se gestionan mediante variables seguras de entorno (`KEYSTORE_PATH`, `STORE_PASSWORD`, `KEY_PASSWORD`) o secrets de la plataforma. Para mantener la seguridad y privacidad del proyecto, las credenciales privadas y claves maestras nunca deben comprometerse ni exponerse en el código fuente ni en el historial de Git.
