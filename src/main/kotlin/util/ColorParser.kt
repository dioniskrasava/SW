package app.sw.util

import androidx.compose.ui.graphics.Color

fun parseColor(colorHex: String): Color {
    return try {
        // Убираем # если есть
        val cleanHex = colorHex.removePrefix("#")

        when (cleanHex.length) {
            6 -> {
                val r = cleanHex.substring(0, 2).toInt(16)
                val g = cleanHex.substring(2, 4).toInt(16)
                val b = cleanHex.substring(4, 6).toInt(16)
                Color(r, g, b)
            }
            8 -> {
                val a = cleanHex.substring(0, 2).toInt(16)
                val r = cleanHex.substring(2, 4).toInt(16)
                val g = cleanHex.substring(4, 6).toInt(16)
                val b = cleanHex.substring(6, 8).toInt(16)
                Color(r, g, b, a)
            }
            else -> Color.Black // fallback
        }
    } catch (e: Exception) {
        Color.Black // fallback цвет при ошибке
    }
}

// Extension функция для удобства
fun String.toColor(): Color = parseColor(this)