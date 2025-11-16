package app.sw.data.model

import kotlinx.serialization.Serializable

@Serializable
data class TimeRecord(
    val id: String,
    val activityId: String,
    val activityName: String, // Добавляем имя активности для истории
    val startTime: Long,
    val endTime: Long,
    val duration: Long,
    val type: RecordType
) {
    companion object {
        fun generateId(): String = System.currentTimeMillis().toString()
    }
}

@Serializable
enum class RecordType {
    START,       // Начало активности
    PAUSE,       // Пауза
    RESET,       // Сброс
    CONTINUE,    // Продолжение после паузы
    COMPLETE,    // Завершение активности
    INACTIVE     // Период бездействия (пауза между активностями)
}