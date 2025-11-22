package app.sw.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.sw.data.model.Activity
import app.sw.util.parseColor

/**
 * Composable элемент для отображения активности в списке управления.
 *
 * Отображает карточку активности с:
 * - Цветным индикатором активности
 * - Названием активности
 * - Индикатором выбранной активности
 * - Кнопкой редактирования
 *
 * Поддерживает взаимодействия:
 * - Выбор активности по клику на карточку
 * - Редактирование по клику на иконку
 *
 * @param activity Активность для отображения
 * @param isSelected Флаг, указывающий выбрана ли данная активность
 * @param onSelect Callback при выборе активности
 * @param onEdit Callback при редактировании активности
 *
 * @sample ActivitiesManagementTab
 * @see Activity
 * @see parseColor
 */
@Composable
fun ActivityManagementItem(
    activity: Activity,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        backgroundColor = if (isSelected) {
            MaterialTheme.colors.primary.copy(alpha = 0.1f)
        } else {
            MaterialTheme.colors.surface
        },
        elevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(parseColor(activity.color))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    activity.name,
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier.weight(1f)
                )
            }

            Row {
                if (isSelected) {
                    Text(
                        "✓ Текущая",
                        color = MaterialTheme.colors.primary,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                }
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        "Edit",
                        tint = MaterialTheme.colors.onSurface
                    )
                }
            }
        }
    }
}