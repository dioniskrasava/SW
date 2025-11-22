package app.sw.util

/**
 * Утилиты для форматирования временных интервалов.
 *
 * Предоставляет функции для преобразования миллисекунд в читабельные
 * строковые представления времени в различных форматах.
 */

/**
 * Форматирует время в формате HH:MM:SS.
 *
 * Преобразует миллисекунды в строку формата "часы:минуты:секунды".
 * Все компоненты времени всегда отображаются двумя цифрами.
 *
 * @param milliseconds Время в миллисекундах для форматирования
 * @return Строка в формате "HH:MM:SS"
 *
 * @sample TimeFormatterTest.formatTime_standard
 * @sample TimeFormatterTest.formatTime_withHours
 *
 * @see formatTimeHumanReadable
 */
fun formatTime(milliseconds: Long): String {
    val totalSeconds = milliseconds / 1000
    val seconds = totalSeconds % 60
    val minutes = (totalSeconds / 60) % 60
    val hours = totalSeconds / 3600

    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

/**
 * Форматирует время в удобочитаемом формате.
 *
 * Возвращает строку, адаптированную под величину временного интервала:
 * - Для интервалов более часа: "X ч Y мин Z сек"
 * - Для интервалов более минуты: "X мин Y сек"
 * - Для малых интервалов: "X сек"
 *
 * @param milliseconds Время в миллисекундах для форматирования
 * @return Строка в удобочитаемом формате
 *
 * @sample TimeFormatterTest.formatTimeHumanReadable_variousCases
 * @see formatTime
 */
fun formatTimeHumanReadable(milliseconds: Long): String {
    val totalSeconds = milliseconds / 1000
    val seconds = totalSeconds % 60
    val minutes = (totalSeconds / 60) % 60
    val hours = totalSeconds / 3600

    return when {
        hours > 0 -> String.format("%d ч %d мин %d сек", hours, minutes, seconds)
        minutes > 0 -> String.format("%d мин %d сек", minutes, seconds)
        else -> String.format("%d сек", seconds)
    }
}