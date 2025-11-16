package app.sw.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.sw.data.model.Activity
import app.sw.data.repository.ActivityRepository
import app.sw.ui.main.StopwatchState

@Composable
fun SettingsScreen(
    stopwatchState: StopwatchState,
    repository: ActivityRepository,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activities by remember { mutableStateOf(repository.loadActivities()) }
    var showEditor by remember { mutableStateOf(false) }
    var editingActivity by remember { mutableStateOf<Activity?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, "Back", tint = MaterialTheme.colors.onBackground)
            }
            Text(
                "Настройки",
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.onBackground
            )
            IconButton(onClick = {
                showEditor = true
                editingActivity = null
            }) {
                Icon(Icons.Default.Add, "Add Activity", tint = MaterialTheme.colors.onBackground)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Activity Selection
        Text(
            "Выберите активность:",
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.colors.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Activities List
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            activities.forEach { activity ->
                ActivityItem(
                    activity = activity,
                    isSelected = stopwatchState.selectedActivityId == activity.id,
                    onSelect = { stopwatchState.setSelectedActivity(activity.id) },
                    onEdit = {
                        editingActivity = activity
                        showEditor = true
                    }
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            if (activities.isEmpty()) {
                Text(
                    "Нет активностей. Нажмите + чтобы добавить.",
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onBackground.copy(alpha = 0.7f),
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // Selected Activity Info
        stopwatchState.selectedActivityId?.let { selectedId ->
            activities.find { it.id == selectedId }?.let { activity ->
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    backgroundColor = MaterialTheme.colors.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(app.sw.util.parseColor(activity.color))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Текущая: ${activity.name}",
                            style = MaterialTheme.typography.body1,
                            color = MaterialTheme.colors.onSurface
                        )
                    }
                }
            }
        }
    }

    // Activity Editor Dialog
    if (showEditor) {
        ActivityEditor(
            activity = editingActivity,
            onSave = { activity ->
                val newActivities = activities.toMutableList()
                if (editingActivity == null) {
                    // Add new
                    newActivities.add(activity)
                } else {
                    // Update existing
                    val index = newActivities.indexOfFirst { it.id == editingActivity?.id }
                    if (index != -1) {
                        newActivities[index] = activity
                    }
                }
                activities = newActivities
                repository.saveActivities(newActivities)
                showEditor = false
                editingActivity = null
            },
            onDelete = { activity ->
                val newActivities = activities.toMutableList()
                newActivities.removeAll { it.id == activity.id }
                activities = newActivities
                repository.saveActivities(newActivities)

                // Clear selection if deleted activity was selected
                if (stopwatchState.selectedActivityId == activity.id) {
                    stopwatchState.setSelectedActivity(null)
                }
                showEditor = false
                editingActivity = null
            },
            onDismiss = {
                showEditor = false
                editingActivity = null
            }
        )
    }
}

@Composable
private fun ActivityItem(
    activity: Activity,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        backgroundColor = if (isSelected) {
            MaterialTheme.colors.primary.copy(alpha = 0.2f)
        } else {
            MaterialTheme.colors.surface
        },
        elevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(app.sw.util.parseColor(activity.color))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    activity.name,
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier.weight(1f)
                )
            }

            Row {
                IconButton(
                    onClick = onSelect,
                    modifier = Modifier.size(36.dp)
                ) {
                    Text(
                        if (isSelected) "✓" else "○",
                        color = if (isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface
                    )
                }
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        "Edit",
                        tint = MaterialTheme.colors.onSurface
                    )
                }
            }
        }
    }
}