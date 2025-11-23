package app.sw.ui.main

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.sw.data.repository.ActivityRepository
import app.sw.ui.settings.SettingsScreen

import androidx.compose.ui.window.WindowState
import androidx.compose.ui.unit.DpSize
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.filter

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
    windowState: WindowState, // Новый параметр вместо callback
    modifier: Modifier = Modifier
) {
    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Main) }

    // LaunchedEffect для отслеживания изменений размера ОКНА ПОЛЬЗОВАТЕЛЕМ
    LaunchedEffect(windowState) {
        snapshotFlow { windowState.size } // Превращаем размер окна в поток данных
            .debounce(500) // Ждем 500мс тишины (чтобы пользователь закончил тянуть окно)
            .collect { newSize ->
                val settings = repository.loadSettings()
                // Проверяем, изменился ли размер по сравнению с сохраненным
                // Это важно, чтобы не сохранять дефолтные значения при запуске

                val currentWidth = newSize.width.value.toInt()
                val currentHeight = newSize.height.value.toInt()

                // Определяем, какие именно настройки обновлять,
                // в зависимости от того, на каком мы экране
                val newSettings = when (currentScreen) {
                    is AppScreen.Main -> {
                        // Если мы на главном экране - сохраняем его размеры
                        if (settings.mainWindowWidth != currentWidth || settings.mainWindowHeight != currentHeight) {
                            settings.copy(
                                mainWindowWidth = currentWidth,
                                mainWindowHeight = currentHeight
                            )
                        } else null
                    }
                    is AppScreen.Settings -> {
                        // Если в настройках - сохраняем размеры настроек
                        if (settings.settingsWindowWidth != currentWidth || settings.settingsWindowHeight != currentHeight) {
                            settings.copy(
                                settingsWindowWidth = currentWidth,
                                settingsWindowHeight = currentHeight
                            )
                        } else null
                    }
                }

                // Если есть что обновлять - сохраняем
                if (newSettings != null) {
                    repository.saveSettings(newSettings)
                    println("Размер окна сохранен: $currentWidth x $currentHeight")
                }
            }
    }

    // Логика изменения размера при ПЕРЕКЛЮЧЕНИИ ЭКРАНОВ
    // (Когда мы сами просим программу изменить размер)
    LaunchedEffect(currentScreen) {
        val settings = repository.loadSettings()
        val targetWidth = if (currentScreen is AppScreen.Settings) settings.settingsWindowWidth else settings.mainWindowWidth
        val targetHeight = if (currentScreen is AppScreen.Settings) settings.settingsWindowHeight else settings.mainWindowHeight

        // Программно меняем размер окна
        windowState.size = DpSize(targetWidth.dp, targetHeight.dp)
    }

    // Отрисовка экранов
    when (currentScreen) {
        is AppScreen.Main -> {
            StopwatchScreen(
                stopwatchState = stopwatchState,
                repository = repository,
                onSettingsClick = {
                    currentScreen = AppScreen.Settings
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
                },
                modifier = modifier
            )
        }
    }
}