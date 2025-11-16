package app.sw.ui.main

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.sw.data.repository.ActivityRepository
import app.sw.ui.settings.SettingsScreen

sealed class AppScreen {
    object Main : AppScreen()
    object Settings : AppScreen()
}

@Composable
fun StopwatchApp(
    stopwatchState: StopwatchState,
    repository: ActivityRepository,
    onWindowResize: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Main) }
    var isExpanded by remember { mutableStateOf(false) }

    // Размеры окна в зависимости от режима
    LaunchedEffect(isExpanded) {
        val width = if (isExpanded) 500 else 400
        val height = if (isExpanded) 500 else 140
        onWindowResize(width, height)
    }

    when (currentScreen) {
        is AppScreen.Main -> {
            StopwatchScreen(
                stopwatchState = stopwatchState,
                repository = repository,
                onSettingsClick = {
                    currentScreen = AppScreen.Settings
                    isExpanded = true
                },
                modifier = modifier
            )
        }
        is AppScreen.Settings -> {
            SettingsScreen(
                stopwatchState = stopwatchState,
                repository = repository,
                onBackClick = {
                    currentScreen = AppScreen.Main
                    isExpanded = false
                },
                modifier = modifier
            )
        }
    }
}