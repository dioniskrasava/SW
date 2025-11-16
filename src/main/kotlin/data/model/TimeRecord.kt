package app.sw.data.model

import kotlinx.serialization.Serializable

@Serializable
data class TimeRecord(
    val id: String,
    val activityId: String,
    val startTime: Long,
    val endTime: Long,
    val duration: Long,
    val type: RecordType, // Добавляем тип записи
    val note: String = "" // Опциональное поле для заметок
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
    COMPLETE     // Завершение активности
}