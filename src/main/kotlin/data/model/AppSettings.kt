package app.sw.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val isActivityTrackingEnabled: Boolean = false,
    val showLogsInMainScreen: Boolean = true
) {
    companion object {
        fun default() = AppSettings()
    }
}