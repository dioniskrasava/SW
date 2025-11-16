package app.sw

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.awt.Dimension
import javax.swing.SwingUtilities

// Константы для цветов
private val DarkBackground = Color(0xFF121212)
private val DarkSurface = Color(0xFF1E1E1E)
private val PrimaryBlue = Color(0xFF64B5F6)
private val TextOnBackground = Color(0xFF81B4FD)
private val InactiveButtonColor = Color(0xFF011731)
private val ActiveButtonColor = Color(0xFF995E05)
private val ButtonTextColor = Color(0xFFADC1D7)

// Константы для размеров
private const val WINDOW_WIDTH = 270
private const val WINDOW_HEIGHT = 140
private val PADDING = 6.dp
private val BUTTON_SPACING = 16.dp
private val TEXT_FONT_SIZE = 42.sp

@Composable
@Preview
fun StopwatchApp() {
    val stopwatchState = rememberStopwatchState()

    MaterialTheme(
        colors = androidx.compose.material.darkColors(
            primary = PrimaryBlue,
            background = DarkBackground,
            surface = DarkSurface,
            onPrimary = Color.White,
            onBackground = TextOnBackground,
            onSurface = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
                .padding(PADDING),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TimeDisplay(displayTime = stopwatchState.displayTime)
            ControlButtons(stopwatchState = stopwatchState)
        }
    }
}

@Composable
private fun TimeDisplay(displayTime: Long) {
    Text(
        text = formatTime(displayTime),
        modifier = Modifier.padding(PADDING),
        fontSize = TEXT_FONT_SIZE,
        color = MaterialTheme.colors.onBackground
    )
}

@Composable
private fun ControlButtons(stopwatchState: StopwatchState) {
    Row(horizontalArrangement = Arrangement.spacedBy(BUTTON_SPACING)) {
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
        enabled = !stopwatchState.isRunning,
        isActive = stopwatchState.isRunning
    )
}

@Composable
private fun PauseButton(stopwatchState: StopwatchState) {
    StopwatchButton(
        text = "Pause",
        onClick = { stopwatchState.pause() },
        enabled = stopwatchState.isRunning,
        isActive = !stopwatchState.isRunning
    )
}

@Composable
private fun ResetButton(stopwatchState: StopwatchState) {
    StopwatchButton(
        text = "Reset",
        onClick = { stopwatchState.reset() },
        enabled = true,
        isActive = false
    )
}

@Composable
private fun StopwatchButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean,
    isActive: Boolean
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (isActive) ActiveButtonColor else InactiveButtonColor,
            contentColor = ButtonTextColor,
            disabledBackgroundColor = ActiveButtonColor
        )
    ) {
        Text(text)
    }
}

@Composable
private fun rememberStopwatchState(): StopwatchState {
    var isRunning by remember { mutableStateOf(false) }
    var displayTime by remember { mutableStateOf(0L) }
    var accumulatedTime by remember { mutableStateOf(0L) }
    var startTime by remember { mutableStateOf(0L) }

    val coroutineScope = rememberCoroutineScope()
    var job by remember { mutableStateOf<Job?>(null) }

    return remember {
        object : StopwatchState {
            override val isRunning: Boolean
                get() = isRunning
            override val displayTime: Long
                get() = displayTime

            override fun start() {
                if (!isRunning) {
                    isRunning = true
                    startTime = System.currentTimeMillis() - accumulatedTime
                    job = coroutineScope.launch {
                        while (isRunning) {
                            displayTime = System.currentTimeMillis() - startTime
                            delay(10)
                        }
                    }
                }
            }

            override fun pause() {
                isRunning = false
                job?.cancel()
                accumulatedTime = displayTime
            }

            override fun reset() {
                if (isRunning) {
                    accumulatedTime = 0
                    startTime = System.currentTimeMillis()
                    displayTime = 0
                } else {
                    job?.cancel()
                    accumulatedTime = 0
                    displayTime = 0
                }
            }
        }
    }
}

private interface StopwatchState {
    val isRunning: Boolean
    val displayTime: Long
    fun start()
    fun pause()
    fun reset()
}

private fun formatTime(milliseconds: Long): String {
    val totalSeconds = milliseconds / 1000
    val seconds = totalSeconds % 60
    val minutes = (totalSeconds / 60) % 60
    val hours = totalSeconds / 3600

    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

fun main() {
    SwingUtilities.invokeLater {
        val window = androidx.compose.ui.awt.ComposeWindow().apply {
            title = "Секундомер"
            minimumSize = Dimension(WINDOW_WIDTH, WINDOW_HEIGHT)
            size = Dimension(WINDOW_WIDTH, WINDOW_HEIGHT)
            setLocationRelativeTo(null)
            defaultCloseOperation = javax.swing.JFrame.EXIT_ON_CLOSE

            setContent {
                StopwatchApp()
            }

            isVisible = true
        }
    }
}