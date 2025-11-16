package app.sw.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.sw.data.model.RecordType
import app.sw.ui.main.LogEntry
import app.sw.util.formatTime

@Composable
fun ActivityLogs(
    logs: List<LogEntry>,
    modifier: Modifier = Modifier
) {
    if (logs.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Нет записей",
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.5f),
                style = MaterialTheme.typography.body2
            )
        }
        return
    }

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(logs) { log ->
            LogItem(log = log)
        }
    }
}

@Composable
private fun LogItem(log: LogEntry) {
    Card(
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Префикс в зависимости от типа
            val prefix = when (log.type) {
                RecordType.START -> "--"
                RecordType.PAUSE -> "=="
                RecordType.RESET -> "--"
                RecordType.CONTINUE -> "--"
                RecordType.COMPLETE -> "++"
            }

            // Текст в зависимости от типа
            val typeText = when (log.type) {
                RecordType.START -> "начало"
                RecordType.PAUSE -> "пауза"
                RecordType.RESET -> "сброс"
                RecordType.CONTINUE -> "продолжение"
                RecordType.COMPLETE -> "завершение"
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$prefix ${log.activityName} $prefix",
                    color = MaterialTheme.colors.onSurface,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = formatTime(log.duration),
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
                    fontSize = 12.sp
                )
            }

            Text(
                text = "($typeText)",
                color = when (log.type) {
                    RecordType.PAUSE -> Color(0xFFFF9800) // Оранжевый для паузы
                    RecordType.RESET -> Color(0xFFF44336) // Красный для сброса
                    RecordType.CONTINUE -> Color(0xFF4CAF50) // Зеленый для продолжения
                    else -> MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                },
                fontSize = 11.sp,
                style = MaterialTheme.typography.caption
            )
        }
    }
}