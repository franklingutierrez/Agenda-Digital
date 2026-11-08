package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EisenhowerQuadrant
import com.example.data.model.LabelEntity
import com.example.data.model.TaskEntity
import com.example.ui.components.PastelBadge
import com.example.ui.components.TaskItemCard
import com.example.ui.viewmodel.AgendaUiState
import com.example.ui.viewmodel.AgendaViewModel

enum class ProductivityViewMode(val label: String, val icon: String) {
    KANBAN("Kanban", "📋"),
    EISENHOWER("Eisenhower", "🎯"),
    LIST("Lista", "📝")
}

@Composable
fun TasksScreen(
    uiState: AgendaUiState,
    viewModel: AgendaViewModel,
    modifier: Modifier = Modifier
) {
    var viewMode by remember { mutableStateOf(ProductivityViewMode.KANBAN) }
    var selectedKanbanColumn by remember { mutableStateOf("ALL") }

    val isDark = MaterialTheme.colorScheme.background.red < 0.5f

    val baseTasks = uiState.filteredTasks

    val completedCount = baseTasks.count { it.isCompleted || it.kanbanStatus == "DONE" }
    val xpPoints = completedCount * 15
    val productivityLevel = when {
        completedCount >= 5 -> "Maestro del Tiempo 🏆"
        completedCount >= 3 -> "Estratega Enfocado ⚡"
        completedCount >= 1 -> "En Ritmo de Acción 🚀"
        else -> "Preparando el Día 🌱"
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Título y selector de metodología
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "Metodología Productiva",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Aprovecha tu tiempo con enfoque y claridad estratégica.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Selector de Modo: Kanban / Matriz Eisenhower / Lista
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(4.dp)
                ) {
                    ProductivityViewMode.values().forEach { mode ->
                        val isSelected = viewMode == mode
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                                .clickable { viewMode = mode }
                                .padding(vertical = 8.dp)
                                .testTag("tasks_view_${mode.name.lowercase()}")
                        ) {
                            Text(
                                text = "${mode.icon} ${mode.label}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Tarjeta de Superación Personal y Gamificación
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFEF08A))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.EmojiEvents,
                                        contentDescription = null,
                                        tint = Color(0xFF854D0E),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = productivityLevel,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "$xpPoints Puntos de Productividad • $completedCount/${baseTasks.size} tareas",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LinearProgressIndicator(
                            progress = { if (baseTasks.isEmpty()) 0f else (completedCount.toFloat() / baseTasks.size.toFloat()) },
                            color = MaterialTheme.colorScheme.secondary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Tip de productividad
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Lightbulb, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Consejo: Enfoque 80/20. Prioriza hoy las tareas de 'Hacer Ahora' para un 80% de impacto.",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Filtro por etiquetas pastel
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    val isAllTags = uiState.selectedLabelFilterId == null
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isAllTags) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { viewModel.selectLabelFilter(null) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Todas las etiquetas",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    uiState.labels.forEach { label ->
                        PastelBadge(
                            label = label,
                            isSelected = uiState.selectedLabelFilterId == label.id,
                            onClick = { viewModel.selectLabelFilter(label.id) }
                        )
                    }
                }
            }

            // CONTENIDO SEGÚN LA METODOLOGÍA SELECCIONADA
            when (viewMode) {
                ProductivityViewMode.KANBAN -> {
                    // TABLERO KANBAN (Estilo Trello)
                    val todoTasks = baseTasks.filter { (it.kanbanStatus == "TODO" || it.kanbanStatus.isEmpty()) && !it.isCompleted }
                    val inProgressTasks = baseTasks.filter { it.kanbanStatus == "IN_PROGRESS" && !it.isCompleted }
                    val doneTasks = baseTasks.filter { it.kanbanStatus == "DONE" || it.isCompleted }

                    // Selector de columna Kanban
                    item {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf(
                                "ALL" to "Todas (${baseTasks.size})",
                                "TODO" to "📋 Por Hacer (${todoTasks.size})",
                                "IN_PROGRESS" to "🚀 En Progreso (${inProgressTasks.size})",
                                "DONE" to "✅ Hechas (${doneTasks.size})"
                            ).forEach { (col, title) ->
                                val isSelected = selectedKanbanColumn == col
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable { selectedKanbanColumn = col }
                                        .padding(vertical = 8.dp)
                                ) {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // Columna 1: Por Hacer (TODO)
                    if (selectedKanbanColumn == "ALL" || selectedKanbanColumn == "TODO") {
                        item {
                            KanbanColumnHeader(
                                title = "📋 Por Hacer (Backlog)",
                                count = todoTasks.size,
                                badgeColor = Color(0xFF93C5FD)
                            )
                        }

                        if (todoTasks.isEmpty()) {
                            item {
                                Text(
                                    text = "No tienes tareas en cola.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                                )
                            }
                        } else {
                            items(todoTasks, key = { it.id }) { task ->
                                val label = uiState.labels.find { it.id == task.labelId }
                                KanbanTaskCard(
                                    task = task,
                                    label = label,
                                    onMoveToInProgress = { viewModel.moveTaskToKanban(task, "IN_PROGRESS") },
                                    onMoveToDone = { viewModel.moveTaskToKanban(task, "DONE") },
                                    onEdit = { viewModel.openCreateTaskDialog(task) },
                                    onDelete = { viewModel.deleteTask(task.id) }
                                )
                            }
                        }
                    }

                    // Columna 2: En Progreso (IN_PROGRESS)
                    if (selectedKanbanColumn == "ALL" || selectedKanbanColumn == "IN_PROGRESS") {
                        item {
                            KanbanColumnHeader(
                                title = "🚀 En Progreso (Ejecutando Ahora)",
                                count = inProgressTasks.size,
                                badgeColor = Color(0xFFFDBA74)
                            )
                        }

                        if (inProgressTasks.isEmpty()) {
                            item {
                                Text(
                                    text = "No tienes tareas en ejecución. ¡Elige una de 'Por Hacer' y pulsa Empezar!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                                )
                            }
                        } else {
                            items(inProgressTasks, key = { it.id }) { task ->
                                val label = uiState.labels.find { it.id == task.labelId }
                                KanbanTaskCard(
                                    task = task,
                                    label = label,
                                    onMoveToInProgress = null,
                                    onMoveToDone = { viewModel.moveTaskToKanban(task, "DONE") },
                                    onEdit = { viewModel.openCreateTaskDialog(task) },
                                    onDelete = { viewModel.deleteTask(task.id) }
                                )
                            }
                        }
                    }

                    // Columna 3: Completadas (DONE)
                    if (selectedKanbanColumn == "ALL" || selectedKanbanColumn == "DONE") {
                        item {
                            KanbanColumnHeader(
                                title = "✅ Completadas (Objetivos Logrados)",
                                count = doneTasks.size,
                                badgeColor = Color(0xFF86EFAC)
                            )
                        }

                        if (doneTasks.isEmpty()) {
                            item {
                                Text(
                                    text = "Aún no has completado tareas hoy.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                                )
                            }
                        } else {
                            items(doneTasks, key = { it.id }) { task ->
                                val label = uiState.labels.find { it.id == task.labelId }
                                KanbanTaskCard(
                                    task = task,
                                    label = label,
                                    onMoveToInProgress = { viewModel.moveTaskToKanban(task, "TODO") },
                                    onMoveToDone = null,
                                    onEdit = { viewModel.openCreateTaskDialog(task) },
                                    onDelete = { viewModel.deleteTask(task.id) }
                                )
                            }
                        }
                    }
                }

                ProductivityViewMode.EISENHOWER -> {
                    // MATRIZ DE EISENHOWER CON COLORES ADAPTABLES
                    val quadrants = if (isDark) {
                        listOf(
                            Triple(EisenhowerQuadrant.DO_FIRST, Color(0xFF3B181B), Color(0xFFFCA5A5)),
                            Triple(EisenhowerQuadrant.SCHEDULE, Color(0xFF132847), Color(0xFF93C5FD)),
                            Triple(EisenhowerQuadrant.DELEGATE, Color(0xFF382A0E), Color(0xFFFEF08A)),
                            Triple(EisenhowerQuadrant.ELIMINATE, Color(0xFF0F3221), Color(0xFF86EFAC))
                        )
                    } else {
                        listOf(
                            Triple(EisenhowerQuadrant.DO_FIRST, Color(0xFFFEE2E2), Color(0xFF991B1B)),
                            Triple(EisenhowerQuadrant.SCHEDULE, Color(0xFFDBEAFE), Color(0xFF1E40AF)),
                            Triple(EisenhowerQuadrant.DELEGATE, Color(0xFFFEF3C7), Color(0xFF92400E)),
                            Triple(EisenhowerQuadrant.ELIMINATE, Color(0xFFD1FAE5), Color(0xFF166534))
                        )
                    }

                    quadrants.forEach { (quadrant, bgColor, titleColor) ->
                        val tasksInQ = baseTasks.filter { it.eisenhowerQuadrant == quadrant.name }

                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = bgColor),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 10.dp)
                                 ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = quadrant.emoji, fontSize = 20.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = quadrant.title,
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                color = titleColor
                                            )
                                            Text(
                                                text = quadrant.subtitle,
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(if (isDark) MaterialTheme.colorScheme.surfaceVariant else Color.White.copy(alpha = 0.9f))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "${tasksInQ.size}",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }

                        if (tasksInQ.isEmpty()) {
                            item {
                                Text(
                                    text = "Sin tareas en este cuadrante.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(start = 12.dp, bottom = 8.dp)
                                )
                            }
                        } else {
                            items(tasksInQ, key = { it.id }) { task ->
                                val label = uiState.labels.find { it.id == task.labelId }
                                TaskItemCard(
                                    task = task,
                                    label = label,
                                    onToggleCompleted = { viewModel.toggleTask(task) },
                                    onEdit = { viewModel.openCreateTaskDialog(task) },
                                    onDelete = { viewModel.deleteTask(task.id) },
                                    onAdvanceKanban = { viewModel.advanceTaskKanban(task) }
                                )
                            }
                        }
                    }
                }

                ProductivityViewMode.LIST -> {
                    // Vista de Lista Estándar
                    if (baseTasks.isEmpty()) {
                        item {
                            EmptyStatePlaceholder(
                                title = "No hay tareas registradas",
                                subtitle = "Toca el botón '+' para añadir una nueva meta."
                            )
                        }
                    } else {
                        items(baseTasks, key = { it.id }) { task ->
                            val label = uiState.labels.find { it.id == task.labelId }
                            TaskItemCard(
                                task = task,
                                label = label,
                                onToggleCompleted = { viewModel.toggleTask(task) },
                                onEdit = { viewModel.openCreateTaskDialog(task) },
                                onDelete = { viewModel.deleteTask(task.id) },
                                onAdvanceKanban = { viewModel.advanceTaskKanban(task) }
                            )
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { viewModel.openCreateTaskDialog() },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("tasks_fab")
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Añadir Tarea")
        }
    }
}

@Composable
fun KanbanColumnHeader(
    title: String,
    count: Int,
    badgeColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 4.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(badgeColor.copy(alpha = 0.35f))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                text = "$count",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun KanbanTaskCard(
    task: TaskEntity,
    label: LabelEntity?,
    onMoveToInProgress: (() -> Unit)?,
    onMoveToDone: (() -> Unit)?,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background.red < 0.5f

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted || task.kanbanStatus == "DONE") {
                if (isDark) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = if (isDark) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (label != null) {
                    PastelBadge(label = label)
                }

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar", modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )

            if (task.description.isNotBlank()) {
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Botones de acción rápida estilo Trello
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (onMoveToInProgress != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f))
                            .clickable { onMoveToInProgress() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (task.kanbanStatus == "DONE") "↺ Reabrir" else "▶ Empezar",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                if (onMoveToDone != null) {
                    val isDark = MaterialTheme.colorScheme.background.red < 0.5f
                    val doneBg = if (isDark) Color(0xFF064E3B) else Color(0xFFD1FAE5)
                    val doneText = if (isDark) Color(0xFF6EE7B7) else Color(0xFF166534)

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(doneBg)
                            .clickable { onMoveToDone() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = doneText, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Completar",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = doneText
                            )
                        }
                    }
                }
            }
        }
    }
}
