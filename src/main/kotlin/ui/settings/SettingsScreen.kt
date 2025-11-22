package app.sw.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.sw.data.model.Activity
import app.sw.data.repository.ActivityRepository
import app.sw.ui.main.StopwatchState

/**
 * Главный экран настроек приложения с вкладками.
 *
 * Предоставляет интерфейс для настройки различных аспектов приложения:
 * - Основные настройки (включение трекинга активностей)
 * - Управление активностями (создание, редактирование, удаление)
 *
 * Использует TabRow для организации настроек по категориям.
 *
 * @param stopwatchState Состояние секундомера для синхронизации настроек
 * @param repository Репозиторий для управления данными активностей
 * @param onBackClick Callback для возврата на главный экран
 * @param modifier Модификатор для настройки layout
 *
 * @sample StopwatchApp
 * @see GeneralSettingsTab
 * @see ActivitiesManagementTab
 * @see StopwatchState
 */
@Composable
fun SettingsScreen(
    stopwatchState: StopwatchState,
    repository: ActivityRepository,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Основные", "Активности", "Окна")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Text("←", color = MaterialTheme.colors.onBackground, fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Настройки",
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.onBackground
            )
        }

        // Tabs
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = selectedTab == index,
                    onClick = { selectedTab = index }
                )
            }
        }

        // Tab Content
        when (selectedTab) {
            0 -> GeneralSettingsTab(stopwatchState = stopwatchState)
            1 -> ActivitiesManagementTab(
                stopwatchState = stopwatchState,
                repository = repository
            )
            2 -> WindowSettingsTab(
                repository = repository
            )
        }
    }
}

/**
 * Вкладка основных настроек приложения.
 *
 * Содержит переключатели и настройки, влияющие на базовую функциональность:
 * - Включение/выключение трекинга активностей
 * - Отображение логов на главном экране
 *
 * @param stopwatchState Состояние для управления настройками
 *
 * @see SettingsScreen
 * @see StopwatchState
 */
@Composable
private fun GeneralSettingsTab(
    stopwatchState: StopwatchState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Основные настройки",
            style = MaterialTheme.typography.h6,
            color = MaterialTheme.colors.onBackground
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Чекбокс включения трекинга активностей
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = stopwatchState.isActivityTrackingEnabled,
                onCheckedChange = { stopwatchState.setActivityTrackingEnabled(it) },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colors.primary
                )
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    "Включить выбор активностей",
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onBackground
                )
                Text(
                    "Позволяет выбирать активности и вести логи времени",
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onBackground.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (stopwatchState.isActivityTrackingEnabled) {
            Text(
                "Теперь в основном окне можно выбирать активности и просматривать логи",
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.primary,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

/**
 * Вкладка управления активностями.
 *
 * Предоставляет интерфейс для CRUD операций с активностями:
 * - Просмотр списка активностей
 * - Добавление новых активностей
 * - Редактирование существующих
 * - Удаление активностей
 * - Выбор текущей активности
 *
 * @param stopwatchState Состояние для синхронизации выбранной активности
 * @param repository Репозиторий для сохранения изменений
 *
 * @see SettingsScreen
 * @see ActivityManagementItem
 * @see ActivityEditor
 */
@Composable
private fun ActivitiesManagementTab(
    stopwatchState: StopwatchState,
    repository: ActivityRepository
) {
    var activities by remember { mutableStateOf(repository.loadActivities()) }
    var showEditor by remember { mutableStateOf(false) }
    var editingActivity by remember { mutableStateOf<Activity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Управление активностями",
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.onBackground
            )
            Button(
                onClick = {
                    showEditor = true
                    editingActivity = null
                }
            ) {
                Text("Добавить")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (activities.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Нет активностей. Нажмите 'Добавить' чтобы создать первую.",
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onBackground.copy(alpha = 0.7f)
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                activities.forEach { activity ->
                    ActivityManagementItem(
                        activity = activity,
                        isSelected = stopwatchState.selectedActivityId == activity.id,
                        onSelect = { stopwatchState.setSelectedActivity(activity.id) },
                        onEdit = {
                            editingActivity = activity
                            showEditor = true
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        // Activity Editor Dialog
        if (showEditor) {
            ActivityEditor(
                activity = editingActivity,
                onSave = { activity ->
                    val newActivities = activities.toMutableList()
                    if (editingActivity == null) {
                        newActivities.add(activity)
                    } else {
                        val index = newActivities.indexOfFirst { it.id == editingActivity?.id }
                        if (index != -1) {
                            newActivities[index] = activity
                        }
                    }
                    activities = newActivities
                    repository.saveActivities(newActivities)
                    showEditor = false
                    editingActivity = null
                },
                onDelete = { activity ->
                    val newActivities = activities.toMutableList()
                    newActivities.removeAll { it.id == activity.id }
                    activities = newActivities
                    repository.saveActivities(newActivities)

                    if (stopwatchState.selectedActivityId == activity.id) {
                        stopwatchState.setSelectedActivity(null)
                    }
                    showEditor = false
                    editingActivity = null
                },
                onDismiss = {
                    showEditor = false
                    editingActivity = null
                }
            )
        }
    }
}