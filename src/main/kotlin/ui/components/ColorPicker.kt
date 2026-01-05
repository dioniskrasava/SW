package app.sw.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.sw.util.parseColor

/**
 * Публичный компонент для выбора цвета из палитры.
 * Может использоваться в разных частях приложения.
 *
 * @param selectedColor Выбранный цвет в HEX-формате
 * @param onColorSelected Callback при выборе цвета
 * @param colors Список доступных цветов (по умолчанию стандартная палитра)
 */
@Composable
fun ColorPicker(
    selectedColor: String,
    onColorSelected: (String) -> Unit,
    colors: List<String> = listOf(
        "#FF5252", "#FF9800", "#FFEB3B", "#4CAF50",
        "#2196F3", "#3F51B5", "#9C27B0", "#E91E63",
        "#795548", "#607D8B", "#00BCD4", "#8BC34A"
    )
) {
    // Разбиваем на ряды по 4 цвета
    val rows = colors.chunked(4)

    Column {
        rows.forEach { rowColors ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                rowColors.forEach { colorHex ->
                    ColorItem(
                        colorHex = colorHex,
                        isSelected = selectedColor == colorHex,
                        onSelected = { onColorSelected(colorHex) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ColorItem(
    colorHex: String,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(
                parseColor(colorHex),
                shape = MaterialTheme.shapes.small
            )
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface.copy(alpha = 0.3f),
                shape = MaterialTheme.shapes.small
            )
            .clickable { onSelected() },
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Text(
                "✓",
                color = Color.White,
                style = MaterialTheme.typography.body2
            )
        }
    }
}