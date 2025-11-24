package app.sw.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.sw.data.model.RecordType
import app.sw.data.model.TimeRecord
import app.sw.i18n.LocalizationManager
import app.sw.util.formatTimeHumanReadable
import java.text.SimpleDateFormat
import java.util.*

/**
 * Обновленный список логов с современным дизайном.
 */
@Composable
fun ActivityLogs(
    logs: List<TimeRecord>,
    modifier: Modifier = Modifier
) {
    if (logs.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "История пуста",
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.3f),
                style = MaterialTheme.typography.body1
            )
        }
        return
    }

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp), // Чуть больше воздуха между карточками
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(logs) { log ->
            ModernLogItem(log = log)
        }
    }
}

@Composable
private fun ModernLogItem(log: TimeRecord) {

    val strings = LocalizationManager.currentResources

    // Определяем стиль в зависимости от типа записи
    val (icon, color, label) = when (log.type) {
        RecordType.START -> Triple(Icons.Default.PlayArrow, Color(0xFF4CAF50), strings.start)
        RecordType.CONTINUE -> Triple(Icons.Default.PlayArrow, Color(0xFF81C784), strings.continue_text) // нужно добавить в Strings.kt
        RecordType.PAUSE -> Triple(Icons.Default.Pause, Color(0xFFFF9800), strings.pause)
        RecordType.RESET -> Triple(Icons.Default.Refresh, Color(0xFFE57373), strings.reset)
        RecordType.COMPLETE -> Triple(Icons.Default.CheckCircle, Color(0xFF2196F3), strings.complete) // нужно добавить
        RecordType.INACTIVE -> Triple(Icons.Default.TimerOff, Color.Gray, strings.inactivity)
    }

    // Форматируем время начала для отображения (например "14:30")
    val startTimeStr = rememberFormattedTime(log.startTime)

    Card(
        elevation = 2.dp,
        shape = MaterialTheme.shapes.medium,
        backgroundColor = MaterialTheme.colors.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min) // Чтобы высота полоски совпадала с карточкой
        ) {
            // 1. Цветная полоска слева (Акцент)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(6.dp)
                    .background(color)
            )

            // 2. Основной контент
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Левая часть: Иконка и Название
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Круглый фон для иконки
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(color.copy(alpha = 0.15f), shape = MaterialTheme.shapes.small),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = if (log.type == RecordType.INACTIVE) strings.idle else log.activityName,
                            style = MaterialTheme.typography.subtitle2,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colors.onSurface
                        )
                        // Мелкий текст: Время события и тип
                        Text(
                            text = "$startTimeStr • $label",
                            style = MaterialTheme.typography.caption,
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }

                // Правая часть: Длительность (если есть)
                if (log.duration > 0) {
                    Surface(
                        color = MaterialTheme.colors.background, // Чуть темнее фон для времени
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(
                            text = formatTimeHumanReadable(log.duration),
                            style = MaterialTheme.typography.body2,
                            fontFamily = FontFamily.Monospace, // Моноширинный шрифт для цифр
                            color = MaterialTheme.colors.onBackground,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

// Вспомогательная функция для форматирования времени (hh:mm:ss)
@Composable
fun rememberFormattedTime(timestamp: Long): String {
    return java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(Date(timestamp))
}