package app.sw.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
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
            .padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Settings button in top right
        Box(modifier = Modifier.fillMaxWidth()) {
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Settings"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TimeDisplay(displayTime = stopwatchState.displayTime)
        Spacer(modifier = Modifier.height(16.dp))
        ControlButtons(stopwatchState = stopwatchState)

        // Activity selection (will be visible only in expanded mode)
        if (stopwatchState.selectedActivityId != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Activity selected",
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
        modifier = Modifier.padding(6.dp),
        fontSize = 42.sp,
        color = MaterialTheme.colors.onBackground
    )
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