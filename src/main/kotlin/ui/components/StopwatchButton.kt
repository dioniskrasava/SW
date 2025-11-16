package app.sw.ui.components

import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val InactiveButtonColor = Color(0xFF011731)
private val ActiveButtonColor = Color(0xFF1491F6)
private val ButtonTextColor = Color(0xFFADC1D7)

@Composable
fun StopwatchButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (enabled) InactiveButtonColor else ActiveButtonColor,
            contentColor = ButtonTextColor,
            disabledBackgroundColor = ActiveButtonColor
        )
    ) {
        Text(text)
    }
}