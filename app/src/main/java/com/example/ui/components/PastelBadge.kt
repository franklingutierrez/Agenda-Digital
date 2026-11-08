package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LabelEntity

import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star

fun parseColorHex(hex: String, fallback: Color = Color(0xFF93C5FD)): Color {
    return try {
        val cleanHex = if (hex.startsWith("#")) hex else "#$hex"
        Color(android.graphics.Color.parseColor(cleanHex))
    } catch (_: Exception) {
        fallback
    }
}

fun getIconForName(iconName: String): ImageVector {
    return when (iconName.lowercase()) {
        "work", "trabajo" -> Icons.Default.Work
        "fitnesscenter", "fitness", "deporte", "ejercicio" -> Icons.Default.FitnessCenter
        "person", "personal", "familia" -> Icons.Default.Person
        "school", "estudio", "universidad" -> Icons.Default.School
        "payments", "finanzas", "dinero", "banco" -> Icons.Default.Payments
        "home", "casa", "hogar" -> Icons.Default.Home
        "shoppingcart", "compras", "tienda" -> Icons.Default.ShoppingCart
        "localhospital", "hospital", "salud", "medicina", "médico" -> Icons.Default.LocalHospital
        "favorite", "amor", "bienestar", "corazón" -> Icons.Default.Favorite
        "flight", "viaje", "turismo", "vuelo" -> Icons.Default.Flight
        "restaurant", "comida", "cena", "almuerzo", "nutrición" -> Icons.Default.Restaurant
        "palette", "arte", "diseño", "creatividad" -> Icons.Default.Palette
        "menubook", "lectura", "libro", "aprender" -> Icons.Default.MenuBook
        "musicnote", "música", "canción", "concierto" -> Icons.Default.MusicNote
        "computer", "tecnología", "código", "software" -> Icons.Default.Computer
        "star", "importante", "favorito", "destacado" -> Icons.Default.Star
        "priorityhigh", "urgente", "prioridad" -> Icons.Default.PriorityHigh
        else -> Icons.Default.Bookmark
    }
}

@Composable
fun getAdaptiveBadgeColors(hex: String, isSelected: Boolean): Pair<Color, Color> {
    val rawColor = parseColorHex(hex)
    val isDark = MaterialTheme.colorScheme.background.red < 0.5f // detect dark theme reliably

    val cleanHex = if (hex.startsWith("#")) hex.uppercase() else "#${hex.uppercase()}"

    return when {
        isSelected -> {
            // Cuando la etiqueta está seleccionada como filtro
            if (isDark) {
                // En tema oscuro: fondo vibrante pastel con texto oscuro de alto contraste
                rawColor to Color(0xFF1E1730)
            } else {
                rawColor to Color(0xFF1E1730)
            }
        }
        isDark -> {
            // Modo Oscuro: El fondo es un tinte sutil y el texto es brillante y perfectamente legible
            when (cleanHex) {
                "#FEF08A" -> Color(0xFF3F350B) to Color(0xFFFEF08A) // Amarillo pastel
                "#FDE047" -> Color(0xFF3F350B) to Color(0xFFFEF08A) // Amarillo canario
                "#FDBA74" -> Color(0xFF43220A) to Color(0xFFFDBA74) // Naranja pastel
                "#FB923C" -> Color(0xFF43220A) to Color(0xFFFDBA74) // Coral durazno
                "#86EFAC" -> Color(0xFF0F3A22) to Color(0xFF86EFAC) // Menta pastel
                "#4ADE80" -> Color(0xFF0B331D) to Color(0xFF86EFAC) // Verde esmeralda
                "#93C5FD" -> Color(0xFF132F52) to Color(0xFF93C5FD) // Azul cielo
                "#60A5FA" -> Color(0xFF132F52) to Color(0xFF93C5FD) // Azul brillante
                "#FCA5A5" -> Color(0xFF45191C) to Color(0xFFFCA5A5) // Rosa pastel
                "#F472B6" -> Color(0xFF45152B) to Color(0xFFF472B6) // Rosa chicle
                "#C4B5FD" -> Color(0xFF2E2250) to Color(0xFFDDD6FE) // Lavanda pastel
                "#A78BFA" -> Color(0xFF28194B) to Color(0xFFDDD6FE) // Violeta pastel
                "#5EEAD4" -> Color(0xFF0D3734) to Color(0xFF5EEAD4) // Turquesa
                "#2DD4BF" -> Color(0xFF0B3330) to Color(0xFF5EEAD4) // Aqua
                "#E9D5FF" -> Color(0xFF381F4E) to Color(0xFFF3E8FF) // Malva
                "#D8B4FE" -> Color(0xFF381F4E) to Color(0xFFF3E8FF) // Lila
                else -> Color(0xFF2E3242) to rawColor
            }
        }
        else -> {
            // Modo Claro: Fondo suave con texto profundo y oscuro de alto contraste
            when (cleanHex) {
                "#FEF08A" -> Color(0xFFFEF9C3) to Color(0xFF713F12) // Amarillo pastel
                "#FDE047" -> Color(0xFFFEF9C3) to Color(0xFF713F12)
                "#FDBA74" -> Color(0xFFFFEDD5) to Color(0xFF9A3412) // Naranja pastel
                "#FB923C" -> Color(0xFFFFEDD5) to Color(0xFF9A3412)
                "#86EFAC" -> Color(0xFFDCFCE7) to Color(0xFF166534) // Menta pastel
                "#4ADE80" -> Color(0xFFDCFCE7) to Color(0xFF166534)
                "#93C5FD" -> Color(0xFFDBEAFE) to Color(0xFF1E40AF) // Celeste pastel
                "#60A5FA" -> Color(0xFFDBEAFE) to Color(0xFF1E40AF)
                "#FCA5A5" -> Color(0xFFFEE2E2) to Color(0xFF991B1B) // Rosa pastel
                "#F472B6" -> Color(0xFFFCE7F3) to Color(0xFF9D174D) // Rosa
                "#C4B5FD" -> Color(0xFFEDE9FE) to Color(0xFF5B21B6) // Lavanda pastel
                "#A78BFA" -> Color(0xFFEDE9FE) to Color(0xFF5B21B6)
                "#5EEAD4" -> Color(0xFFCCFBF1) to Color(0xFF115E59) // Turquesa
                "#2DD4BF" -> Color(0xFFCCFBF1) to Color(0xFF115E59)
                "#E9D5FF" -> Color(0xFFF3E8FF) to Color(0xFF6B21A8) // Malva
                "#D8B4FE" -> Color(0xFFF3E8FF) to Color(0xFF6B21A8)
                else -> rawColor.copy(alpha = 0.22f) to Color(0xFF1E1730)
            }
        }
    }
}

@Composable
fun PastelBadge(
    label: LabelEntity,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, contentColor) = getAdaptiveBadgeColors(label.colorHex, isSelected)

    val clickableModifier = if (onClick != null) {
        modifier.clickable { onClick() }
    } else {
        modifier
    }

    Box(
        modifier = clickableModifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = getIconForName(label.iconName),
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = " " + label.name,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 12.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                ),
                color = contentColor
            )
        }
    }
}

@Composable
fun PastelDot(
    colorHex: String,
    modifier: Modifier = Modifier
) {
    val color = parseColorHex(colorHex)
    Box(
        modifier = modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(color)
    )
}
