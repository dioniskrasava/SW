package app.sw.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import app.sw.data.model.Activity
import kotlin.random.Random

@Composable
fun ActivityEditor(
    activity: Activity?,
    onSave: (Activity) -> Unit,
    onDelete: (Activity) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(activity?.name ?: "") }
    var color by remember { mutableStateOf(activity?.color ?: generateRandomColor()) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = 8.dp,
            backgroundColor = MaterialTheme.colors.surface
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    if (activity == null) "Новая активность" else "Редактирование",
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Name Input
                Text(
                    "Название:",
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurface
                )
                BasicTextField(
                    value = name,
                    onValueChange = { name = it },
                    textStyle = TextStyle(color = MaterialTheme.colors.onSurface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.background)
                        .padding(8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Color Selection
                Text(
                    "Цвет:",
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val colors = listOf(
                        "#FF5252", "#FF9800", "#FFEB3B", "#4CAF50",
                        "#2196F3", "#3F51B5", "#9C27B0", "#E91E63"
                    )

                    colors.forEach { colorHex ->
                        val isSelected = color == colorHex
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(app.sw.util.parseColor(colorHex)) // ← исправлено здесь
                                .then(
                                    if (isSelected) {
                                        Modifier.border(
                                            2.dp,
                                            MaterialTheme.colors.primary,
                                            MaterialTheme.shapes.small
                                        )
                                    } else {
                                        Modifier
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Text("✓", color = Color.White)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Buttons
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (activity != null) {
                        IconButton(
                            onClick = { onDelete(activity) },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                "Delete",
                                tint = MaterialTheme.colors.error
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.background
                        ),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Отмена", color = MaterialTheme.colors.onBackground)
                    }

                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                val updatedActivity = Activity(
                                    id = activity?.id ?: Activity.generateId(),
                                    name = name,
                                    color = color
                                )
                                onSave(updatedActivity)
                            }
                        },
                        enabled = name.isNotBlank()
                    ) {
                        Text(if (activity == null) "Добавить" else "Сохранить")
                    }
                }
            }
        }
    }
}

private fun generateRandomColor(): String {
    val colors = listOf(
        "#FF5252", "#FF9800", "#FFEB3B", "#4CAF50",
        "#2196F3", "#3F51B5", "#9C27B0", "#E91E63"
    )
    return colors[Random.nextInt(colors.size)]
}