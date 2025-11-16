package app.sw.util

fun formatTime(milliseconds: Long): String {
    val totalSeconds = milliseconds / 1000
    val seconds = totalSeconds % 60
    val minutes = (totalSeconds / 60) % 60
    val hours = totalSeconds / 3600

    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

// Дополнительная функция для красивого отображения времени
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