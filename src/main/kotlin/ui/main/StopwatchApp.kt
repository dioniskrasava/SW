package app.sw.ui.main

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.sw.data.repository.ActivityRepository
import app.sw.ui.settings.SettingsScreen

/**
 * Перечисление экранов приложения.
 *
 * Определяет возможные состояния навигации в приложении.
 * Используется для управления переключением между основными разделами.
 *
 * @property Main Главный экран секундомера
 * @property Settings Экран настроек приложения
 */
sealed class AppScreen {
    object Main : AppScreen()
    object Settings : AppScreen()
}

/**
 * Главный композейбл приложения, управляющий навигацией и размерами окна.
 *
 * Координирует переключение между экранами и управляет размером окна в зависимости
 * от текущего состояния. Автоматически изменяет размеры окна при переходе между
 * компактным (главный экран) и расширенным (настройки) режимами.
 *
 * @param stopwatchState Состояние секундомера, передаваемое во все экраны
 * @param repository Репозиторий данных для доступа к активностям и настройкам
 * @param onWindowResize Callback для изменения размеров окна
 * @param modifier Модификатор для настройки layout
 *
 * @sample app.sw.main
 * @see StopwatchScreen
 * @see SettingsScreen
 * @see StopwatchState
 * @see AppScreen
 */
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