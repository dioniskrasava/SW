package app.sw.ui.main

import app.sw.data.model.TimeRecord
import app.sw.data.repository.ActivityRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.*

interface StopwatchState {
    val isRunning: Boolean
    val displayTime: Long
    val selectedActivityId: String?
    fun start()
    fun pause()
    fun reset()
    fun setSelectedActivity(activityId: String?)
}

@Composable
fun rememberStopwatchState(repository: ActivityRepository): StopwatchState {
    var isRunning by remember { mutableStateOf(false) }
    var displayTime by remember { mutableStateOf(0L) }
    var accumulatedTime by remember { mutableStateOf(0L) }
    var startTime by remember { mutableStateOf(0L) }
    var selectedActivityId by remember { mutableStateOf<String?>(null) }
    var currentRecordId by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()
    var job by remember { mutableStateOf<Job?>(null) }

    return remember {
        object : StopwatchState {
            override val isRunning: Boolean
                get() = isRunning
            override val displayTime: Long
                get() = displayTime
            override val selectedActivityId: String?
                get() = selectedActivityId

            override fun start() {
                if (!isRunning) {
                    isRunning = true
                    startTime = System.currentTimeMillis() - accumulatedTime

                    // Create new time record if activity is selected
                    selectedActivityId?.let { activityId ->
                        currentRecordId = TimeRecord.generateId()
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

                // Save time record if activity is selected and timer was running
                selectedActivityId?.let { activityId ->
                    currentRecordId?.let { recordId ->
                        if (displayTime > 0) {
                            val record = TimeRecord(
                                id = recordId,
                                activityId = activityId,
                                startTime = startTime,
                                endTime = System.currentTimeMillis(),
                                duration = displayTime
                            )
                            repository.addTimeRecord(record)
                            currentRecordId = null
                        }
                    }
                }
            }

            override fun reset() {
                if (isRunning) {
                    // Если секундомер работает - сбрасываем время, но продолжаем отсчет
                    accumulatedTime = 0
                    startTime = System.currentTimeMillis()
                    displayTime = 0

                    // Если активность выбрана, создаем новую запись для нового отсчета
                    selectedActivityId?.let { activityId ->
                        // Сохраняем текущую запись (если она есть)
                        currentRecordId?.let { recordId ->
                            if (displayTime > 0) {
                                val record = TimeRecord(
                                    id = recordId,
                                    activityId = activityId,
                                    startTime = startTime - displayTime, // Корректируем startTime
                                    endTime = System.currentTimeMillis(),
                                    duration = displayTime
                                )
                                repository.addTimeRecord(record)
                            }
                        }
                        // Создаем новую запись для нового отсчета
                        currentRecordId = TimeRecord.generateId()
                    }
                } else {
                    // Если секундомер не работает - полный сброс
                    job?.cancel()
                    accumulatedTime = 0
                    displayTime = 0
                    currentRecordId = null
                }
            }

            override fun setSelectedActivity(activityId: String?) {
                // Если таймер работает и мы меняем активность, сохраняем текущую запись
                if (isRunning && selectedActivityId != null && selectedActivityId != activityId) {
                    selectedActivityId?.let { currentActivityId ->
                        currentRecordId?.let { recordId ->
                            if (displayTime > 0) {
                                val record = TimeRecord(
                                    id = recordId,
                                    activityId = currentActivityId,
                                    startTime = startTime,
                                    endTime = System.currentTimeMillis(),
                                    duration = displayTime
                                )
                                repository.addTimeRecord(record)
                            }
                        }
                    }

                    // Сбрасываем для новой активности
                    accumulatedTime = 0
                    displayTime = 0
                    startTime = System.currentTimeMillis()
                    currentRecordId = TimeRecord.generateId()
                }

                selectedActivityId = activityId
            }
        }
    }
}