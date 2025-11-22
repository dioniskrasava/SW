package app.sw.util

import androidx.compose.ui.graphics.Color

/**
 * Утилиты для работы с цветами в приложении.
 *
 * Предоставляет функции для преобразования строковых представлений цветов
 * в объекты [Color] Compose и обратно.
 */

/**
 * Преобразует строку с HEX-кодом цвета в объект [Color].
 *
 * Поддерживает форматы:
 * - 6 символов (RGB): "#RRGGBB" или "RRGGBB"
 * - 8 символов (ARGB): "#AARRGGBB" или "AARRGGBB"
 *
 * @param colorHex Строка с HEX-кодом цвета, может содержать символ '#' в начале
 * @return Объект Color или [Color.Black] в случае ошибки парсинга
 *
 * @throws NumberFormatException если HEX-строка содержит недопустимые символы
 * @sample ColorParserTest.parseColor_validFormats
 *
 * @see Color
 * @see String.toColor
 */
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

/**
 * Extension-функция для удобного преобразования строки в цвет.
 *
 * Позволяет использовать syntax: `"#FF5252".toColor()`
 *
 * @receiver Строка с HEX-кодом цвета
 * @return Объект Color, полученный через [parseColor]
 *
 * @sample ColorParserTest.extensionFunction
 * @see parseColor
 */
fun String.toColor(): Color = parseColor(this)