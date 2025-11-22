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
 * @see AppSettings
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

    // Загружаем текущие настройки для размеров окон
    val settings = repository.loadSettings()

    // Размеры окна в зависимости от режима
    LaunchedEffect(isExpanded, settings) {
        val width = if (isExpanded) settings.settingsWindowWidth else settings.mainWindowWidth
        val height = if (isExpanded) settings.settingsWindowHeight else settings.mainWindowHeight
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