package app.sw.ui.main

import app.sw.data.model.*
import app.sw.data.repository.ActivityRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.*

/**
 * Интерфейс состояния секундомера.
 *
 * Определяет контракт для управления секундомером и отслеживания его состояния.
 * Используется для разделения логики состояния и UI компонентов.
 *
 * @property isRunning Флаг, указывающий запущен ли секундомер в данный момент
 * @property displayTime Текущее отображаемое время в миллисекундах
 * @property selectedActivityId ID выбранной активности или null если не выбрана
 * @property activityLogs Список временных записей для отображения истории
 * @property isActivityTrackingEnabled Включен ли трекинг активностей
 * @property inactiveTime Общее время, проведенное в состоянии паузы/бездействия
 *
 * @see rememberStopwatchState
 * @see TimeRecord
 * @see Activity
 */
interface StopwatchState {
    val isRunning: Boolean
    val displayTime: Long
    val selectedActivityId: String?
    val activityLogs: List<TimeRecord>
    val isActivityTrackingEnabled: Boolean
    val inactiveTime: Long



    /**
     * Запускает секундомер.
     *
     * Если секундомер был на паузе, добавляет запись о бездействии.
     * Создает запись START или CONTINUE в зависимости от предыдущего состояния.
     */
    fun start()

    /**
     * Приостанавливает секундомер.
     *
     * Сохраняет текущее время и создает запись PAUSE.
     * Запоминает время начала паузы для последующего учета бездействия.
     */
    fun pause()

    /**
     * Сбрасывает секундомер.
     *
     * В режиме работы: сбрасывает время, но продолжает отсчет.
     * В режиме паузы: полностью обнуляет состояние.
     * Создает запись RESET.
     */
    fun reset()

    /**
     * Устанавливает выбранную активность.
     *
     * При смене активности во время работы автоматически завершает предыдущую
     * активность и начинает новую. Сбрасывает время для новой активности.
     *
     * @param activityId ID активности или null для сброса выбора
     */
    fun setSelectedActivity(activityId: String?)

    /**
     * Очищает историю временных записей.
     *
     * Удаляет все записи о работе секундомера и сбрасывает счетчик бездействия.
     */
    fun clearLogs()

    /**
     * Включает или отключает трекинг активностей.
     *
     * При отключении сбрасывает выбранную активность.
     *
     * @param enabled true для включения трекинга, false для отключения
     */
    fun setActivityTrackingEnabled(enabled: Boolean)
}

/**
 * Создает и запоминает состояние секундомера.
 *
 * Фабричная функция, которая инициализирует и управляет жизненным циклом
 * состояния секундомера. Использует Compose runtime для реактивного обновления UI.
 *
 * @param repository Репозиторий для загрузки и сохранения данных
 * @return Экземпляр [StopwatchState] для использования в композиции
 *
 * @sample StopwatchScreen
 * @see StopwatchState
 * @see ActivityRepository
 */
@Composable
fun rememberStopwatchState(repository: ActivityRepository): StopwatchState {
    var isRunning by remember { mutableStateOf(false) }
    var displayTime by remember { mutableStateOf(0L) }
    var accumulatedTime by remember { mutableStateOf(0L) }
    var selectedActivityId by remember { mutableStateOf<String?>(null) }
    var currentRecordId by remember { mutableStateOf<String?>(null) }
    var inactiveTime by remember { mutableStateOf(0L) }
    var lastPauseStart by remember { mutableStateOf(0L) }

    // Загружаем настройки
    var appSettings by remember { mutableStateOf(repository.loadSettings()) }

    val coroutineScope = rememberCoroutineScope()
    var job by remember { mutableStateOf<Job?>(null) }

    // ДОБАВЬТЕ ЭТИ ДВЕ ПЕРЕМЕННЫЕ
    var startTime by remember { mutableStateOf(0L) }
    var baseDuration by remember { mutableStateOf(0L) }

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
                if (isRunning) return

                // 1. Обработка времени бездействия с момента последней паузы
                if (lastPauseStart > 0) {
                    val pauseDuration = System.currentTimeMillis() - lastPauseStart
                    if (pauseDuration >= 1000) {
                        addInactiveRecord(pauseDuration)
                    }
                    lastPauseStart = 0
                }

                // 2. Устанавливаем флаг и фиксируем МОМЕНТ АБСОЛЮТНОГО СТАРТА
                isRunning = true
                // Мы фиксируем, когда мы начали отсчет (накопленное время уже сохранено в accumulatedTime)
                startTime = System.currentTimeMillis()

                // 3. Определяем тип записи
                val recordType = if (accumulatedTime > 0) RecordType.CONTINUE else RecordType.START

                // 4. Логирование
                selectedActivityId?.let { activityId ->
                    val activities = repository.loadActivities()
                    val activity = activities.find { it.id == activityId }
                    activity?.let { addRecord(activityId, it.name, recordType) }
                }

                // 5. Запускаем точный таймер (переходим к Шагу 3)
                startTickingJob()
            }


            override fun pause() {
                if (!isRunning) return

                // 1. Отменяем работу корутины
                isRunning = false
                job?.cancel()

                // 2. Вычисляем точную длительность и сохраняем ее как накопленное время (базу)
                val elapsedSinceStart = System.currentTimeMillis() - startTime
                accumulatedTime += elapsedSinceStart // ВАЖНО: прибавляем прошедшее время к старой базе
                displayTime = accumulatedTime // Обновляем дисплей на окончательное, точное время

                // 3. Фиксируем время паузы для трекинга бездействия
                lastPauseStart = System.currentTimeMillis()

                // 4. Логирование
                selectedActivityId?.let { activityId ->
                    val activities = repository.loadActivities()
                    val activity = activities.find { it.id == activityId }
                    activity?.let { addRecord(activityId, it.name, RecordType.PAUSE, displayTime) }
                }
            }


            override fun reset() {
                // 1. Вычисляем длительность для логирования
                val resetDuration = if (isRunning) accumulatedTime + (System.currentTimeMillis() - startTime) else displayTime

                // 2. Очищаем все счетчики
                accumulatedTime = 0L
                displayTime = 0L
                lastPauseStart = 0L

                // 3. Если таймер запущен, сброс происходит "на лету"
                if (isRunning) {
                    // Просто фиксируем новый момент старта, но база уже 0
                    startTime = System.currentTimeMillis()
                } else {
                    // Если не запущен, отменяем работу корутины (для гарантии)
                    job?.cancel()
                }

                // 4. Логирование
                if (resetDuration > 0) {
                    selectedActivityId?.let { activityId ->
                        val activities = repository.loadActivities()
                        val activity = activities.find { it.id == activityId }
                        activity?.let { addRecord(activityId, it.name, RecordType.RESET, resetDuration) }
                    }
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

            private fun startTickingJob() {
                job?.cancel() // Отменяем старую, если была
                job = coroutineScope.launch {
                    // Мы используем более долгий интервал (50 мс), так как нам не нужно тикать каждую 1 мс.
                    // Точность обеспечивается System.currentTimeMillis(), а не delay().
                    while (isRunning) {

                        // Время, прошедшее с момента, как мы нажали START
                        val elapsedSinceStart = System.currentTimeMillis() - startTime

                        // Точное время = (Накопленная база) + (Время, прошедшее с момента старта)
                        displayTime = accumulatedTime + elapsedSinceStart

                        delay(50) // Задержка для плавного обновления UI
                    }
                }
            }
        }
    }
}