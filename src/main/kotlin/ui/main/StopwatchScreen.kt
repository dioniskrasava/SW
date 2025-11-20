package app.sw.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.sw.data.model.Activity
import app.sw.data.repository.ActivityRepository
import app.sw.ui.components.ActivityLogs
import app.sw.ui.components.StopwatchButton
import app.sw.util.formatTime
import app.sw.util.formatTimeHumanReadable
import app.sw.util.parseColor

@Composable
fun StopwatchScreen(
    stopwatchState: StopwatchState,
    repository: ActivityRepository,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Верхняя строка: время и кнопка настроек на одном уровне
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(40.dp)
            )

            TimeDisplay(displayTime = stopwatchState.displayTime)

            SettingsButton(onClick = onSettingsClick)
        }

       // Spacer(modifier = Modifier.height(40.dp))

        // Если включен трекинг активностей, показываем выбор активности
        if (stopwatchState.isActivityTrackingEnabled) {
            ActivitySelector(
                stopwatchState = stopwatchState,
                repository = repository
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Кнопки управления секундомером
        ControlButtons(stopwatchState = stopwatchState)

        // В функции StopwatchScreen заменим блок с логами на:
// Логи активности (только если включен трекинг)
        if (stopwatchState.isActivityTrackingEnabled && stopwatchState.activityLogs.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "История всех активностей:",
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Общее время бездействия
            if (stopwatchState.inactiveTime > 0) {
                Text(
                    text = "Общее время пауз: ${formatTimeHumanReadable(stopwatchState.inactiveTime)}",
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onBackground.copy(alpha = 0.6f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            ActivityLogs(
                logs = stopwatchState.activityLogs,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp, max = 400.dp) // Теперь можно прокручивать все логи
            )

            Spacer(modifier = Modifier.height(12.dp))
            StopwatchButton(
                text = "Очистить историю",
                onClick = { stopwatchState.clearLogs() },
                enabled = true
            )
        }
    }
}

@Composable
private fun TimeDisplay(displayTime: Long) {
    Text(
        text = formatTime(displayTime),
        fontSize = 48.sp,
        color = MaterialTheme.colors.onBackground
    )
}

@Composable
private fun SettingsButton(
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .width(80.dp)
            .height(40.dp),
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colors.onBackground.copy(alpha = 0.7f)
        )
    ) {
        Text(
            text = "⋮",
            fontSize = 28.sp,
            style = MaterialTheme.typography.h6
        )
    }
}

@Composable
private fun ControlButtons(stopwatchState: StopwatchState) {
    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
        StartButton(stopwatchState = stopwatchState)
        PauseButton(stopwatchState = stopwatchState)
        ResetButton(stopwatchState = stopwatchState)
    }
}

@Composable
private fun StartButton(stopwatchState: StopwatchState) {
    StopwatchButton(
        text = "Start",
        onClick = { stopwatchState.start() },
        enabled = !stopwatchState.isRunning
    )
}

@Composable
private fun PauseButton(stopwatchState: StopwatchState) {
    StopwatchButton(
        text = "Pause",
        onClick = { stopwatchState.pause() },
        enabled = stopwatchState.isRunning
    )
}

@Composable
private fun ResetButton(stopwatchState: StopwatchState) {
    StopwatchButton(
        text = "Reset",
        onClick = { stopwatchState.reset() },
        enabled = true
    )
}

@Composable
private fun ActivitySelector(
    stopwatchState: StopwatchState,
    repository: ActivityRepository
) {
    var expanded by remember { mutableStateOf(false) }
    val activities by remember { mutableStateOf(repository.loadActivities()) }

    val selectedActivity = activities.find { it.id == stopwatchState.selectedActivityId }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Текущая активность:",
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.colors.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Button(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.surface,
                    contentColor = MaterialTheme.colors.onSurface
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (selectedActivity != null) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(parseColor(selectedActivity.color))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            selectedActivity.name,
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Text(
                            "Выберите активность",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Text("▼", fontSize = 12.sp)
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                if (activities.isEmpty()) {
                    DropdownMenuItem(onClick = {
                        expanded = false
                    }) {
                        Text("Нет активностей")
                    }
                } else {
                    activities.forEach { activity ->
                        DropdownMenuItem(onClick = {
                            stopwatchState.setSelectedActivity(activity.id)
                            expanded = false
                        }) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .background(parseColor(activity.color))
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    activity.name,
                                    modifier = Modifier.weight(1f)
                                )
                                if (stopwatchState.selectedActivityId == activity.id) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("✓", color = MaterialTheme.colors.primary)
                                }
                            }
                        }
                    }
                }

                Divider()

                DropdownMenuItem(onClick = {
                    // Здесь можно добавить логику для быстрого создания активности
                    expanded = false
                }) {
                    Text("Управление активностями...")
                }
            }
        }

        if (selectedActivity != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Активность '${selectedActivity.name}' выбрана",
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.7f)
            )
        }
    }
}