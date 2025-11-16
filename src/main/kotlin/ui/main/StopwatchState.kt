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
    val activityLogs: List<LogEntry>
    val isActivityTrackingEnabled: Boolean
    fun start()
    fun pause()
    fun reset()
    fun setSelectedActivity(activityId: String?)
    fun clearLogs()
    fun setActivityTrackingEnabled(enabled: Boolean)
}

data class LogEntry(
    val activityName: String,
    val duration: Long,
    val type: RecordType,
    val timestamp: Long = System.currentTimeMillis()
)

@Composable
fun rememberStopwatchState(repository: ActivityRepository): StopwatchState {
    var isRunning by remember { mutableStateOf(false) }
    var displayTime by remember { mutableStateOf(0L) }
    var accumulatedTime by remember { mutableStateOf(0L) }
    var startTime by remember { mutableStateOf(0L) }
    var selectedActivityId by remember { mutableStateOf<String?>(null) }
    var currentRecordId by remember { mutableStateOf<String?>(null) }
    var activityLogs by remember { mutableStateOf<List<LogEntry>>(emptyList()) }

    // Загружаем настройки
    var appSettings by remember { mutableStateOf(repository.loadSettings()) }

    val coroutineScope = rememberCoroutineScope()
    var job by remember { mutableStateOf<Job?>(null) }

    // Функция для сохранения настроек
    fun saveSettings(newSettings: AppSettings) {
        appSettings = newSettings
        repository.saveSettings(newSettings)
    }

    // Функция для добавления записи в логи
    fun addLog(type: RecordType, duration: Long = displayTime) {
        selectedActivityId?.let { activityId ->
            val activities = repository.loadActivities()
            val activity = activities.find { it.id == activityId }
            activity?.let {
                val newLog = LogEntry(
                    activityName = it.name,
                    duration = duration,
                    type = type
                )
                activityLogs = listOf(newLog) + activityLogs // Новые логи добавляются в начало
            }
        }
    }

    // Функция для загрузки логов активности
    fun loadActivityLogs() {
        selectedActivityId?.let { activityId ->
            val activities = repository.loadActivities()
            val activity = activities.find { it.id == activityId }
            val records = repository.loadTimeRecords()
                .filter { it.activityId == activityId }
                .sortedByDescending { it.startTime }

            activityLogs = records.map { record ->
                LogEntry(
                    activityName = activity?.name ?: "Неизвестно",
                    duration = record.duration,
                    type = record.type,
                    timestamp = record.startTime
                )
            }
        } ?: run {
            activityLogs = emptyList()
        }
    }

    // Загружаем логи при изменении выбранной активности
    LaunchedEffect(selectedActivityId) {
        loadActivityLogs()
    }

    return remember {
        object : StopwatchState {
            override val isRunning: Boolean
                get() = isRunning
            override val displayTime: Long
                get() = displayTime
            override val selectedActivityId: String?
                get() = selectedActivityId
            override val activityLogs: List<LogEntry>
                get() = activityLogs
            override val isActivityTrackingEnabled: Boolean
                get() = appSettings.isActivityTrackingEnabled

            override fun start() {
                if (!isRunning) {
                    isRunning = true
                    startTime = System.currentTimeMillis() - accumulatedTime

                    // Определяем тип записи: начало или продолжение
                    val recordType = if (accumulatedTime > 0) RecordType.CONTINUE else RecordType.START

                    // Создаем новую запись если активность выбрана
                    selectedActivityId?.let { activityId ->
                        currentRecordId = TimeRecord.generateId()
                        addLog(recordType)
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

                // Сохраняем запись и добавляем в логи
                selectedActivityId?.let { activityId ->
                    currentRecordId?.let { recordId ->
                        if (displayTime > 0) {
                            val record = TimeRecord(
                                id = recordId,
                                activityId = activityId,
                                startTime = startTime,
                                endTime = System.currentTimeMillis(),
                                duration = displayTime,
                                type = RecordType.PAUSE
                            )
                            repository.addTimeRecord(record)
                            addLog(RecordType.PAUSE, displayTime)
                        }
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
                    addLog(RecordType.RESET, resetDuration)

                    // Если активность выбрана, создаем новую запись для нового отсчета
                    selectedActivityId?.let { activityId ->
                        currentRecordId = TimeRecord.generateId()
                        addLog(RecordType.START)
                    }
                } else {
                    // Если секундомер не работает - полный сброс
                    job?.cancel()
                    accumulatedTime = 0
                    displayTime = 0
                    currentRecordId = null
                    activityLogs = emptyList()
                }
            }

            override fun setSelectedActivity(activityId: String?) {
                selectedActivityId = activityId
                loadActivityLogs()
            }

            override fun clearLogs() {
                activityLogs = emptyList()
            }

            override fun setActivityTrackingEnabled(enabled: Boolean) {
                saveSettings(appSettings.copy(isActivityTrackingEnabled = enabled))
                if (!enabled) {
                    // При отключении трекинга сбрасываем выбранную активность
                    selectedActivityId = null
                    activityLogs = emptyList()
                }
            }
        }
    }
}