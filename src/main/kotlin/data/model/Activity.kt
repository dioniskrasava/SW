package app.sw.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Activity(
    val id: String,
    var name: String,
    val color: String, // hex color
    var isActive: Boolean = true
) {
    companion object {
        fun generateId(): String = System.currentTimeMillis().toString()
    }
}