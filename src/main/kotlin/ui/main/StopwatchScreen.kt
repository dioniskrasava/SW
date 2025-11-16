package app.sw.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.sw.ui.components.StopwatchButton
import app.sw.util.formatTime

@Composable
fun StopwatchScreen(
    stopwatchState: StopwatchState,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Верхняя строка: время и кнопка настроек на одном уровне
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Пустой элемент для балансировки (занимает столько же места, сколько кнопка настроек)
            Box(
                modifier = Modifier
                    .width(60.dp) // Такая же ширина как у кнопки настроек
                    .height(40.dp) // Такая же высота как у кнопки настроек
            )

            // Основное время по центру
            TimeDisplay(displayTime = stopwatchState.displayTime)

            // Кнопка настроек справа
            SettingsButton(onClick = onSettingsClick)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Кнопки управления секундомером
        ControlButtons(stopwatchState = stopwatchState)

        // Информация о выбранной активности (если есть)
        if (stopwatchState.selectedActivityId != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Активность выбрана",
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun TimeDisplay(displayTime: Long) {
    Text(
        text = formatTime(displayTime),
        fontSize = 38.sp, // Немного увеличим шрифт
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
            .width(60.dp) // Фиксированная ширина
            .height(40.dp), // Фиксированная высота
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colors.onBackground.copy(alpha = 0.7f)
        )
    ) {
        Text(
            //text = "⋮", // Юникод символ - три точки по вертикали
            //text = "⋯",
            //text = "⚙",
            //text = "⚙️", // аналог вышестоящего
            text = "☰",
            fontSize = 24.sp,
            style = MaterialTheme.typography.h6
        )
    }
}

@Composable
private fun ControlButtons(stopwatchState: StopwatchState) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
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