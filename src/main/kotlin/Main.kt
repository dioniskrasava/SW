package app.sw

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import app.sw.data.repository.ActivityRepository
import app.sw.ui.main.StopwatchApp
import app.sw.ui.main.rememberStopwatchState
import java.awt.Dimension

private val DarkBackground = androidx.compose.ui.graphics.Color(0xFF121212)
private val DarkSurface = androidx.compose.ui.graphics.Color(0xFF1E1E1E)
private val PrimaryBlue = androidx.compose.ui.graphics.Color(0xFF64B5F6)
private val TextOnBackground = androidx.compose.ui.graphics.Color(0xFF81B4FD)

fun main() = application {
    val repository = remember { ActivityRepository() }
    val stopwatchState = rememberStopwatchState(repository)

    Window(
        title = "Умный секундомер",
        state = rememberWindowState(width = 320.dp, height = 140.dp),
        onCloseRequest = ::exitApplication
    ) {
        MaterialTheme(
            colors = darkColors(
                primary = PrimaryBlue,
                background = DarkBackground,
                surface = DarkSurface,
                onPrimary = androidx.compose.ui.graphics.Color.White,
                onBackground = TextOnBackground,
                onSurface = androidx.compose.ui.graphics.Color.White
            )
        ) {
            StopwatchApp(
                stopwatchState = stopwatchState,
                repository = repository,
                onWindowResize = { width, height ->
                    window.minimumSize = Dimension(width, height)
                    window.size = Dimension(width, height)
                }
            )
        }
    }
}

@Composable
@Preview
fun AppPreview() {
    val repository = ActivityRepository()
    val stopwatchState = rememberStopwatchState(repository)

    MaterialTheme(
        colors = darkColors(
            primary = PrimaryBlue,
            background = DarkBackground,
            surface = DarkSurface,
            onPrimary = androidx.compose.ui.graphics.Color.White,
            onBackground = TextOnBackground,
            onSurface = androidx.compose.ui.graphics.Color.White
        )
    ) {
        StopwatchApp(
            stopwatchState = stopwatchState,
            repository = repository,
            onWindowResize = { _, _ -> }
        )
    }
}