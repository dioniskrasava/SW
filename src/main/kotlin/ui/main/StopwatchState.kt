package app.sw.ui.main

import app.sw.data.model.*
import app.sw.data.repository.ActivityRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.*

interface StopwatchState {
    val isRunning: Boolean
    val displayTime: Long
    val selectedActivityId: String?
    val activityLogs: List<TimeRecord>
    val isActivityTrackingEnabled: Boolean
    val inactiveTime: Long // Время проведенное на паузе

    fun start()
    fun pause()
    fun reset()
    fun setSelectedActivity(activityId: String?)
    fun clearLogs()
    fun setActivityTrackingEnabled(enabled: Boolean)
}

@Composable
fun rememberStopwatchState(repository: ActivityRepository): StopwatchState {
    var isRunning by remember { mutableStateOf(false) }
    var displayTime by remember { mutableStateOf(0L) }
    var accumulatedTime by remember { mutableStateOf(0L) }
    var startTime by remember { mutableStateOf(0L) }
    var selectedActivityId by remember { mutableStateOf<String?>(null) }
    var currentRecordId by remember { mutableStateOf<String?>(null) }
    var inactiveTime by remember { mutableStateOf(0L) }
    var lastPauseStart by remember { mutableStateOf(0L) }

    // Загружаем настройки
    var appSettings by remember { mutableStateOf(repository.loadSettings()) }

    val coroutineScope = rememberCoroutineScope()
    var job by remember { mutableStateOf<Job?>(null) }

    // Функция для сохранения настроек
    fun saveSettings(newSettings: AppSettings) {
        appSettings = newSettings
        repository.saveSettings(newSettings)
    }

    // Функция для загрузки всех логов
    fun loadAllLogs(): List<TimeRecord> {
        return repository.getActivityLogs()
    }

    // Функция для добавления записи в логи
    fun addRecord(activityId: String?, activityName: String, type: RecordType, duration: Long = 0) {
        activityId?.let { id ->
            val record = TimeRecord(
                id = TimeRecord.generateId(),
                activityId = id,
                activityName = activityName,
                startTime = System.currentTimeMillis(),
                endTime = System.currentTimeMillis() + duration,
                duration = duration,
                type = type
            )
            repository.addTimeRecord(record)
        }
    }

    // Функция для добавления записи о бездействии
    fun addInactiveRecord(duration: Long) {
        val record = TimeRecord(
            id = TimeRecord.generateId(),
            activityId = "inactive",
            activityName = "Пауза",
            startTime = System.currentTimeMillis() - duration,
            endTime = System.currentTimeMillis(),
            duration = duration,
            type = RecordType.INACTIVE
        )
        repository.addTimeRecord(record)
        inactiveTime += duration
    }

    return remember {
        object : StopwatchState {
            override val isRunning: Boolean
                get() = isRunning
            override val displayTime: Long
                get() = displayTime
            override val selectedActivityId: String?
                get() = selectedActivityId
            override val activityLogs: List<TimeRecord>
                get() = loadAllLogs()
            override val isActivityTrackingEnabled: Boolean
                get() = appSettings.isActivityTrackingEnabled
            override val inactiveTime: Long
                get() = inactiveTime

            override fun start() {
                if (!isRunning) {
                    // Если было время паузы, добавляем запись о бездействии
                    if (lastPauseStart > 0) {
                        val pauseDuration = System.currentTimeMillis() - lastPauseStart
                        if (pauseDuration > 1000) { // Только если пауза была больше 1 секунды
                            addInactiveRecord(pauseDuration)
                        }
                        lastPauseStart = 0
                    }

                    isRunning = true
                    startTime = System.currentTimeMillis() - accumulatedTime

                    // Определяем тип записи: начало или продолжение
                    val recordType = if (accumulatedTime > 0) RecordType.CONTINUE else RecordType.START

                    // Создаем запись если активность выбрана
                    selectedActivityId?.let { activityId ->
                        val activities = repository.loadActivities()
                        val activity = activities.find { it.id == activityId }
                        activity?.let {
                            addRecord(activityId, it.name, recordType)
                        }
                    }

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
                lastPauseStart = System.currentTimeMillis()

                // Сохраняем запись если активность выбрана
                selectedActivityId?.let { activityId ->
                    val activities = repository.loadActivities()
                    val activity = activities.find { it.id == activityId }
                    activity?.let {
                        addRecord(activityId, it.name, RecordType.PAUSE, displayTime)
                    }
                }
            }

            override fun reset() {
                if (isRunning) {
                    // Если секундомер работает - сбрасываем время, но продолжаем отсчет
                    val resetDuration = displayTime
                    accumulatedTime = 0
                    startTime = System.currentTimeMillis()
                    displayTime = 0

                    // Добавляем запись о сбросе
                    selectedActivityId?.let { activityId ->
                        val activities = repository.loadActivities()
                        val activity = activities.find { it.id == activityId }
                        activity?.let {
                            addRecord(activityId, it.name, RecordType.RESET, resetDuration)
                        }
                    }
                } else {
                    // Если секундомер не работает - полный сброс
                    job?.cancel()
                    accumulatedTime = 0
                    displayTime = 0
                    currentRecordId = null
                    lastPauseStart = 0
                }
            }

            override fun setSelectedActivity(activityId: String?) {
                // Если таймер работает и мы меняем активность, сохраняем текущую активность
                if (isRunning && selectedActivityId != null) {
                    selectedActivityId?.let { currentActivityId ->
                        val activities = repository.loadActivities()
                        val currentActivity = activities.find { it.id == currentActivityId }
                        currentActivity?.let {
                            addRecord(currentActivityId, it.name, RecordType.COMPLETE, displayTime)
                        }
                    }

                    // Сбрасываем для новой активности
                    accumulatedTime = 0
                    displayTime = 0
                    startTime = System.currentTimeMillis()
                }

                selectedActivityId = activityId

                // Начинаем новую активность если таймер работает
                if (isRunning && activityId != null) {
                    val activities = repository.loadActivities()
                    val activity = activities.find { it.id == activityId }
                    activity?.let {
                        addRecord(activityId, it.name, RecordType.START)
                    }
                }
            }

            override fun clearLogs() {
                repository.clearLogs()
                inactiveTime = 0
            }

            override fun setActivityTrackingEnabled(enabled: Boolean) {
                saveSettings(appSettings.copy(isActivityTrackingEnabled = enabled))
                if (!enabled) {
                    // При отключении трекинга сбрасываем выбранную активность
                    selectedActivityId = null
                }
            }
        }
    }
}