package app.sw.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.sw.data.repository.ActivityRepository

/**
 * Вкладка настроек размеров окон приложения.
 *
 * Предоставляет интерфейс для настройки размеров основных окон приложения:
 * - Главное окно секундомера (компактный режим)
 * - Окно настроек (расширенный режим)
 *
 * Изменения применяются немедленно и сохраняются в настройках приложения.
 *
 * @param repository Репозиторий для загрузки и сохранения настроек
 *
 * @see SettingsScreen
 * @see ActivityRepository
 * @see AppSettings
 */
@Composable
fun WindowSettingsTab(
    repository: ActivityRepository
) {
    // Загружаем текущие настройки
    var settings by remember { mutableStateOf(repository.loadSettings()) }

    // Локальные состояния для полей ввода
    var mainWindowWidth by remember { mutableStateOf(settings.mainWindowWidth.toString()) }
    var mainWindowHeight by remember { mutableStateOf(settings.mainWindowHeight.toString()) }
    var settingsWindowWidth by remember { mutableStateOf(settings.settingsWindowWidth.toString()) }
    var settingsWindowHeight by remember { mutableStateOf(settings.settingsWindowHeight.toString()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Настройки размеров окон",
            style = MaterialTheme.typography.h6,
            color = MaterialTheme.colors.onBackground
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Настройки главного окна
        WindowSizeSection(
            title = "Главное окно (секундомер)",
            description = "Размер окна в основном режиме работы",
            widthValue = mainWindowWidth,
            heightValue = mainWindowHeight,
            onWidthChange = { mainWindowWidth = it },
            onHeightChange = { mainWindowHeight = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Настройки окна настроек
        WindowSizeSection(
            title = "Окно настроек",
            description = "Размер окна в расширенном режиме настроек",
            widthValue = settingsWindowWidth,
            heightValue = settingsWindowHeight,
            onWidthChange = { settingsWindowWidth = it },
            onHeightChange = { settingsWindowHeight = it }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Кнопка сохранения
        Button(
            onClick = {
                // Обновляем настройки
                val newSettings = settings.copy(
                    mainWindowWidth = mainWindowWidth.toIntOrNull() ?: 400,
                    mainWindowHeight = mainWindowHeight.toIntOrNull() ?: 140,
                    settingsWindowWidth = settingsWindowWidth.toIntOrNull() ?: 500,
                    settingsWindowHeight = settingsWindowHeight.toIntOrNull() ?: 500
                )
                settings = newSettings
                repository.saveSettings(newSettings)
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Сохранить размеры")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Информация о применении изменений
        Text(
            "Изменения вступят в силу при следующем открытии соответствующего окна",
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onBackground.copy(alpha = 0.7f)
        )
    }
}

/**
 * Секция настройки размеров для конкретного окна.
 *
 * @param title Заголовок секции
 * @param description Описание назначения окна
 * @param widthValue Текущее значение ширины
 * @param heightValue Текущее значение высоты
 * @param onWidthChange Callback при изменении ширины
 * @param onHeightChange Callback при изменении высоты
 */
@Composable
private fun WindowSizeSection(
    title: String,
    description: String,
    widthValue: String,
    heightValue: String,
    onWidthChange: (String) -> Unit,
    onHeightChange: (String) -> Unit
) {
    Column {
        Text(
            title,
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.colors.onBackground
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            description,
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onBackground.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Поле ввода ширины
            OutlinedTextField(
                value = widthValue,
                onValueChange = onWidthChange,
                label = { Text("Ширина (dp)") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )

            // Поле ввода высоты
            OutlinedTextField(
                value = heightValue,
                onValueChange = onHeightChange,
                label = { Text("Высота (dp)") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }

        // Валидация ввода
        if (widthValue.toIntOrNull() == null || heightValue.toIntOrNull() == null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Введите числовые значения",
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.error
            )
        }
    }
}