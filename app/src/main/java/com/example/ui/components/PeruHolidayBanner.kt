package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.provider.PeruHoliday
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

@Composable
fun PeruHolidayBanner(
    holiday: PeruHoliday,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val daysUntil = ChronoUnit.DAYS.between(today, holiday.date)
    val isToday = daysUntil == 0L

    val formatter = DateTimeFormatter.ofPattern("d 'de' MMMM", Locale("es", "PE"))
    val formattedDate = holiday.date.format(formatter)

    val isDark = MaterialTheme.colorScheme.background.red < 0.5f
    val containerBg = if (isDark) Color(0xFF2E151A) else Color(0xFFFFF5F5)
    val borderColor = if (isDark) Color(0xFF881337) else Color(0xFFFECDD3)
    val badgeBg = if (isDark) Color(0xFF4C1D24) else Color(0xFFFFE4E6)
    val primaryTextColor = if (isDark) Color(0xFFFECDD3) else Color(0xFF881337)
    val secondaryTextColor = if (isDark) Color(0xFFFDA4AF) else Color(0xFF9F1239)

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerBg
        ),
        border = BorderStroke(1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(badgeBg)
            ) {
                Text(
                    text = "🇵🇪",
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = if (isToday) "¡Hoy es Feriado Oficial!" else "Próximo Feriado de Perú",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFFFB7185) else Color(0xFFE11D48)
                        )
                    )
                    if (isToday) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (isDark) Color(0xFFFB7185) else Color(0xFFE11D48),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Text(
                    text = holiday.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = primaryTextColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "$formattedDate • ${holiday.description}",
                    style = MaterialTheme.typography.bodySmall,
                    color = secondaryTextColor,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (!isToday && daysUntil > 0) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isDark) Color(0xFF4C1D24) else Color.White)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "En $daysUntil d.",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isDark) Color(0xFFFECDD3) else Color(0xFFBE123C)
                    )
                }
            }
        }
    }
}
