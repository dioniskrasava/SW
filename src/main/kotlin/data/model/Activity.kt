package app.sw.data.model

import kotlinx.serialization.Serializable

/**
 * Модель активности/задачи для трекинга времени.
 *
 * Представляет собой задачу, которую пользователь хочет отслеживать с помощью секундомера.
 * Каждая активность имеет уникальный идентификатор, название, цвет для визуального отображения
 * и статус активности.
 *
 * @property id Уникальный идентификатор активности, генерируется на основе временной метки
 * @property name Название активности, отображается в интерфейсе
 * @property color Цвет активности в HEX-формате (например, "#FF5252")
 * @property isActive Флаг, указывающий активна ли данная задача для выбора
 *
 * @sample app.sw.data.model.Activity.generateId
 */
@Serializable
data class Activity(
    val id: String,
    var name: String,
    val color: String,
    var isActive: Boolean = true
) {
    companion object {
        /**
         * Генерирует уникальный идентификатор активности на основе текущего времени.
         *
         * Использует системное время в миллисекундах для обеспечения уникальности ID.
         * В нормальных условиях гарантирует уникальность, но в случае очень быстрого
         * создания активностей возможны коллизии.
         *
         * @return Уникальный строковый идентификатор
         *
         * @see System.currentTimeMillis
         */
        fun generateId(): String = System.currentTimeMillis().toString()
    }
}