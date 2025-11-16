package app.sw.data.model

import kotlinx.serialization.Serializable

@Serializable
data class TimeRecord(
    val id: String,
    val activityId: String,
    val startTime: Long,
    val endTime: Long,
    val duration: Long
) {
    companion object {
        fun generateId(): String = System.currentTimeMillis().toString()
    }
}