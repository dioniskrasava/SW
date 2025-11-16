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
import app.sw.data.model.TimeRecord
import app.sw.util.formatTime
import app.sw.util.formatTimeHumanReadable

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
                text = "Нет записей",
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.5f),
                style = MaterialTheme.typography.body2
            )
        }
        return
    }

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(logs) { log ->
            LogItem(log = log)
        }
    }
}

@Composable
private fun LogItem(log: TimeRecord) {
    Card(
        backgroundColor = when (log.type) {
            RecordType.INACTIVE -> MaterialTheme.colors.background
            else -> MaterialTheme.colors.surface
        },
        elevation = if (log.type == RecordType.INACTIVE) 0.dp else 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Префикс и текст в зависимости от типа
            val (prefix, mainText, typeText) = when (log.type) {
                RecordType.START -> Triple("--", "${log.activityName}", "начало")
                RecordType.PAUSE -> Triple("==", "${log.activityName}", "пауза")
                RecordType.RESET -> Triple("--", "${log.activityName}", "сброс")
                RecordType.CONTINUE -> Triple("--", "${log.activityName}", "продолжение")
                RecordType.COMPLETE -> Triple("++", "${log.activityName}", "завершение")
                RecordType.INACTIVE -> Triple("••", "Пауза", "бездействие")
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$prefix $mainText $prefix",
                    color = when (log.type) {
                        RecordType.INACTIVE -> MaterialTheme.colors.onBackground.copy(alpha = 0.5f)
                        else -> MaterialTheme.colors.onSurface
                    },
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )

                if (log.type != RecordType.START && log.type != RecordType.CONTINUE) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatTimeHumanReadable(log.duration),
                        color = when (log.type) {
                            RecordType.INACTIVE -> MaterialTheme.colors.onBackground.copy(alpha = 0.4f)
                            else -> MaterialTheme.colors.onSurface.copy(alpha = 0.8f)
                        },
                        fontSize = 12.sp
                    )
                }
            }

            Text(
                text = "($typeText)",
                color = when (log.type) {
                    RecordType.PAUSE -> Color(0xFFFF9800) // Оранжевый для паузы
                    RecordType.RESET -> Color(0xFFF44336) // Красный для сброса
                    RecordType.CONTINUE -> Color(0xFF4CAF50) // Зеленый для продолжения
                    RecordType.INACTIVE -> MaterialTheme.colors.onBackground.copy(alpha = 0.3f)
                    else -> MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                },
                fontSize = 11.sp,
                style = MaterialTheme.typography.caption
            )
        }
    }
}