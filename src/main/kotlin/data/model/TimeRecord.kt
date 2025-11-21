package app.sw.data.model

import kotlinx.serialization.Serializable

/**
 * Модель записи о временном событии секундомера.
 *
 * Фиксирует различные события в работе секундомера (старт, пауза, сброс и т.д.)
 * для последующего анализа и построения истории активности.
 *
 * @property id Уникальный идентификатор записи
 * @property activityId ID связанной активности
 * @property activityName Название активности на момент создания записи (для истории)
 * @property startTime Время начала события в миллисекундах
 * @property endTime Время окончания события в миллисекундах
 * @property duration Продолжительность события в миллисекундах
 * @property type Тип события секундомера
 *
 * @see RecordType
 * @see Activity
 */
@Serializable
data class TimeRecord(
    val id: String,
    val activityId: String,
    val activityName: String,
    val startTime: Long,
    val endTime: Long,
    val duration: Long,
    val type: RecordType
) {
    companion object {
        /**
         * Генерирует уникальный идентификатор для временной записи.
         *
         * @return Строковый ID на основе текущего времени
         */
        fun generateId(): String = System.currentTimeMillis().toString()
    }
}

/**
 * Типы событий секундомера.
 *
 * Определяет возможные действия, которые могут быть зафиксированы в [TimeRecord].
 *
 * @property START Начало отсчета времени для активности
 * @property PAUSE Приостановка отсчета времени
 * @property RESET Сброс секундомера
 * @property CONTINUE Продолжение после паузы
 * @property COMPLETE Завершение активности
 * @property INACTIVE Период бездействия между активностями
 */
@Serializable
enum class RecordType {
    START,
    PAUSE,
    RESET,
    CONTINUE,
    COMPLETE,
    INACTIVE
}