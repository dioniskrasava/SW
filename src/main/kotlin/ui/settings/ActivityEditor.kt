package app.sw.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import app.sw.data.model.Activity
import app.sw.util.parseColor
import kotlin.random.Random

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ActivityEditor(
    activity: Activity?,
    onSave: (Activity) -> Unit,
    onDelete: (Activity) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(activity?.name ?: "") }
    var color by remember { mutableStateOf(activity?.color ?: generateRandomColor()) }

    // Для фокуса и курсора
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Автофокус при открытии
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

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

                // Name Input с улучшенным TextField
                Text(
                    "Название:",
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))

                TextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused) {
                                keyboardController?.show()
                            }
                        },
                    textStyle = TextStyle(color = MaterialTheme.colors.onSurface),
                    placeholder = {
                        Text(
                            "Введите название активности",
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
                        )
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = MaterialTheme.colors.background,
                        cursorColor = MaterialTheme.colors.primary,
                        focusedIndicatorColor = MaterialTheme.colors.primary,
                        unfocusedIndicatorColor = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                            if (name.isNotBlank()) {
                                saveActivity(activity, name, color, onSave)
                            }
                        }
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Color Selection - с исправленной обработкой выбора
                Text(
                    "Цвет:",
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                ColorGrid(
                    selectedColor = color,
                    onColorSelected = { newColor ->
                        color = newColor
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Buttons
                ActionButtons(
                    activity = activity,
                    name = name,
                    onSave = { saveActivity(activity, name, color, onSave) },
                    onDelete = { activity?.let(onDelete) },
                    onDismiss = onDismiss
                )
            }
        }
    }
}

@Composable
private fun ColorGrid(
    selectedColor: String,
    onColorSelected: (String) -> Unit
) {
    val colors = listOf(
        "#FF5252", "#FF9800", "#FFEB3B", "#4CAF50",
        "#2196F3", "#3F51B5", "#9C27B0", "#E91E63",
        "#795548", "#607D8B", "#00BCD4", "#8BC34A"
    )

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

@Composable
private fun ActionButtons(
    activity: Activity?,
    name: String,
    onSave: () -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (activity != null) {
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Удалить",
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
            onClick = onSave,
            enabled = name.isNotBlank()
        ) {
            Text(if (activity == null) "Добавить" else "Сохранить")
        }
    }
}

private fun saveActivity(
    activity: Activity?,
    name: String,
    color: String,
    onSave: (Activity) -> Unit
) {
    if (name.isNotBlank()) {
        val updatedActivity = Activity(
            id = activity?.id ?: Activity.generateId(),
            name = name,
            color = color
        )
        onSave(updatedActivity)
    }
}

private fun generateRandomColor(): String {
    val colors = listOf(
        "#FF5252", "#FF9800", "#FFEB3B", "#4CAF50",
        "#2196F3", "#3F51B5", "#9C27B0", "#E91E63"
    )
    return colors[Random.nextInt(colors.size)]
}