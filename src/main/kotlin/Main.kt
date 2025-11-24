package app.sw

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import app.sw.data.model.AppSettings
import app.sw.data.repository.ActivityRepository
import app.sw.i18n.LocalizationManager
import app.sw.i18n.stringResource
import app.sw.ui.main.StopwatchApp
import app.sw.ui.main.rememberStopwatchState
import java.awt.Dimension

/**
 * Точка входа в приложение "Умный секундомер".
 *
 * Инициализирует и запускает Compose Desktop приложение с темной темой
 * и предопределенными размерами окна. Управляет жизненным циклом приложения
 * и зависимостями (репозиторий, состояние).
 */

// Цветовая палитра приложения
private val DarkBackground = androidx.compose.ui.graphics.Color(0xFF121212)
private val DarkSurface = androidx.compose.ui.graphics.Color(0xFF1E1E1E)
private val PrimaryBlue = androidx.compose.ui.graphics.Color(0xFF64B5F6)
private val TextOnBackground = androidx.compose.ui.graphics.Color(0xFF81B4FD)

/**
 * Главная функция приложения.
 *
 * Создает и настраивает главное окно приложения:
 * - Устанавливает заголовок и начальные размеры
 * - Настраивает Material Theme с темной цветовой схемой
 * - Инициализирует репозиторий данных и состояние приложения
 * - Запускает основной композейбл [StopwatchApp]
 *
 * @see StopwatchApp
 * @see ActivityRepository
 * @see rememberStopwatchState
 * @see AppSettings
 */
fun main() = application {
    val repository = remember { ActivityRepository() }
    val stopwatchState = rememberStopwatchState(repository)

    // Загружаем настройки и устанавливаем язык
    val initialSettings = repository.loadSettings()
    LocalizationManager.setLanguage(initialSettings.language, repository)

    // Создаем состояние окна здесь
    val windowState = rememberWindowState(
        width = initialSettings.mainWindowWidth.dp,
        height = initialSettings.mainWindowHeight.dp
    )

    Window(
        title = stringResource { app_name }, // Используем локализованное название
        state = windowState,
        onCloseRequest = ::exitApplication
    ) {

        // МИНИМАЛЬНЫЙ РАЗМЕР ОКНА
        window.minimumSize = Dimension(
            AppSettings.MIN_WINDOW_WIDTH,
            AppSettings.MIN_WINDOW_HEIGHT
        )

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
                windowState = windowState
            )
        }
    }
}
