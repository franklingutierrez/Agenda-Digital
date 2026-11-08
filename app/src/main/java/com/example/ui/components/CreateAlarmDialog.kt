package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AlarmEntity
import com.example.util.AlarmSoundPlayer
import java.util.Locale

@Composable
fun CreateAlarmDialog(
    alarmToEdit: AlarmEntity?,
    onSaveAlarm: (AlarmEntity) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var hour by remember { mutableIntStateOf(alarmToEdit?.hour ?: 7) }
    var minute by remember { mutableIntStateOf(alarmToEdit?.minute ?: 0) }

    var isManualInputMode by remember { mutableStateOf(false) }
    var manualHourText by remember { mutableStateOf(String.format(Locale.getDefault(), "%02d", hour)) }
    var manualMinuteText by remember { mutableStateOf(String.format(Locale.getDefault(), "%02d", minute)) }

    var label by remember { mutableStateOf(alarmToEdit?.label ?: "Despertador") }
    var vibrate by remember { mutableStateOf(alarmToEdit?.vibrate ?: true) }
    var soundName by remember { mutableStateOf(alarmToEdit?.soundName ?: "Tono Predeterminado del Sistema") }
    var soundUri by remember { mutableStateOf(alarmToEdit?.soundUri ?: "") }

    // Selector de archivos de audio / MP3
    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            soundUri = uri.toString()
            soundName = "MP3 / Audio Personalizado"
            AlarmSoundPlayer.playAlarm(context, soundUri, false)
        }
    }

    val initialDays = alarmToEdit?.repeatDays?.split(",")?.mapNotNull { it.trim().toIntOrNull() }?.toSet() ?: setOf(2, 3, 4, 5, 6)
    var selectedDays by remember { mutableStateOf(initialDays) }

    val dayNames = listOf("Dom" to 1, "Lun" to 2, "Mar" to 3, "Mié" to 4, "Jue" to 5, "Vie" to 6, "Sáb" to 7)

    Dialog(onDismissRequest = {
        AlarmSoundPlayer.stop()
        onDismiss()
    }) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("create_alarm_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (alarmToEdit == null) "Programar Alarma" else "Editar Alarma",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    IconButton(onClick = {
                        AlarmSoundPlayer.stop()
                        onDismiss()
                    }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Selector de modo: Con flechas o Ingreso Manual con teclado
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isManualInputMode) "Modo: Teclado Manual" else "Modo: Selector Visual",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                            .clickable { isManualInputMode = !isManualInputMode }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = if (isManualInputMode) Icons.Default.Schedule else Icons.Default.Edit,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isManualInputMode) "Usar Flechas" else "Escribir Hora",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // CONTENEDOR DE HORA (Visual o Manual)
                if (isManualInputMode) {
                    // ENTRADA MANUAL CON TECLADO
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(16.dp)
                    ) {
                        OutlinedTextField(
                            value = manualHourText,
                            onValueChange = { input ->
                                if (input.length <= 2 && input.all { it.isDigit() }) {
                                    manualHourText = input
                                    input.toIntOrNull()?.let {
                                        if (it in 0..23) hour = it
                                    }
                                }
                            },
                            label = { Text("Hora (0-23)", fontSize = 11.sp) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = MaterialTheme.typography.titleLarge.copy(
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.width(96.dp)
                        )

                        Text(
                            text = " : ",
                            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        OutlinedTextField(
                            value = manualMinuteText,
                            onValueChange = { input ->
                                if (input.length <= 2 && input.all { it.isDigit() }) {
                                    manualMinuteText = input
                                    input.toIntOrNull()?.let {
                                        if (it in 0..59) minute = it
                                    }
                                }
                            },
                            label = { Text("Min (0-59)", fontSize = 11.sp) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = MaterialTheme.typography.titleLarge.copy(
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.width(96.dp)
                        )
                    }
                } else {
                    // SELECTOR CON BOTONES Y FLECHAS
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(14.dp)
                    ) {
                        // Hora
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "▲",
                                modifier = Modifier
                                    .clickable {
                                        hour = (hour + 1) % 24
                                        manualHourText = String.format(Locale.getDefault(), "%02d", hour)
                                    }
                                    .padding(6.dp)
                            )
                            Text(
                                text = String.format(Locale.getDefault(), "%02d", hour),
                                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "▼",
                                modifier = Modifier
                                    .clickable {
                                        hour = if (hour == 0) 23 else hour - 1
                                        manualHourText = String.format(Locale.getDefault(), "%02d", hour)
                                    }
                                    .padding(6.dp)
                            )
                        }

                        Text(
                            text = " : ",
                            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 14.dp)
                        )

                        // Minuto
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "▲",
                                modifier = Modifier
                                    .clickable {
                                        minute = (minute + 5) % 60
                                        manualMinuteText = String.format(Locale.getDefault(), "%02d", minute)
                                    }
                                    .padding(6.dp)
                            )
                            Text(
                                text = String.format(Locale.getDefault(), "%02d", minute),
                                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "▼",
                                modifier = Modifier
                                    .clickable {
                                        minute = if (minute < 5) 55 else minute - 5
                                        manualMinuteText = String.format(Locale.getDefault(), "%02d", minute)
                                    }
                                    .padding(6.dp)
                            )
                        }
                    }
                }

                // Píldoras de horas predefinidas rápidas
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf(
                        "06:00" to (6 to 0),
                        "06:30" to (6 to 30),
                        "07:00" to (7 to 0),
                        "07:30" to (7 to 30),
                        "08:00" to (8 to 0)
                    ).forEach { (labelStr, timePair) ->
                        val isCurrent = hour == timePair.first && minute == timePair.second
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable {
                                    hour = timePair.first
                                    minute = timePair.second
                                    manualHourText = String.format(Locale.getDefault(), "%02d", hour)
                                    manualMinuteText = String.format(Locale.getDefault(), "%02d", minute)
                                }
                                .padding(vertical = 6.dp)
                        ) {
                            Text(
                                text = labelStr,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = if (isCurrent) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("Nombre o motivo de la alarma") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Días de repetición:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    dayNames.forEach { (name, dayNum) ->
                        val isSelected = selectedDays.contains(dayNum)
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable {
                                    selectedDays = if (isSelected) {
                                        selectedDays - dayNum
                                    } else {
                                        selectedDays + dayNum
                                    }
                                }
                        ) {
                            Text(
                                text = name.take(1),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                ),
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Selector de Tono y MP3
                Text(
                    text = "Sonido y Timbre de Alarma:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = soundName,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            maxLines = 1
                        )
                    }

                    Row {
                        IconButton(
                            onClick = {
                                AlarmSoundPlayer.playAlarm(context, soundUri, false)
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Probar sonido", modifier = Modifier.size(18.dp))
                        }
                        IconButton(
                            onClick = { audioPickerLauncher.launch("audio/*") },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(imageVector = Icons.Default.UploadFile, contentDescription = "Elegir MP3", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Vibración / Zumbido al sonar",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Switch(
                        checked = vibrate,
                        onCheckedChange = { vibrate = it }
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        AlarmSoundPlayer.stop()
                        val repeatStr = selectedDays.sorted().joinToString(",")
                        val alarm = alarmToEdit?.copy(
                            hour = hour,
                            minute = minute,
                            label = label.trim().ifEmpty { "Despertador" },
                            repeatDays = repeatStr.ifEmpty { "1,2,3,4,5,6,7" },
                            vibrate = vibrate,
                            soundName = soundName,
                            soundUri = soundUri,
                            isEnabled = true
                        ) ?: AlarmEntity(
                            hour = hour,
                            minute = minute,
                            label = label.trim().ifEmpty { "Despertador" },
                            repeatDays = repeatStr.ifEmpty { "1,2,3,4,5,6,7" },
                            vibrate = vibrate,
                            soundName = soundName,
                            soundUri = soundUri,
                            isEnabled = true
                        )
                        onSaveAlarm(alarm)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_alarm_btn")
                ) {
                    Text(if (alarmToEdit == null) "Activar Alarma" else "Guardar Cambios")
                }
            }
        }
    }
}
