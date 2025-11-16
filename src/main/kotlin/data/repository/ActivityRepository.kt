package app.sw.data.repository

import app.sw.data.model.Activity
import app.sw.data.model.AppSettings
import app.sw.data.model.TimeRecord
import kotlinx.serialization.json.Json
import java.io.File

class ActivityRepository {
    private val activitiesFile = File("activities.json")
    private val timeRecordsFile = File("time_records.json")
    private val settingsFile = File("app_settings.json")
    private val json = Json { prettyPrint = true }

    // Activities (без изменений)
    fun saveActivities(activities: List<Activity>) {
        try {
            activitiesFile.writeText(json.encodeToString(ListSerializer(Activity.serializer()), activities))
        } catch (e: Exception) {
            println("Error saving activities: ${e.message}")
        }
    }

    fun loadActivities(): List<Activity> {
        return if (activitiesFile.exists()) {
            try {
                json.decodeFromString(ListSerializer(Activity.serializer()), activitiesFile.readText())
            } catch (e: Exception) {
                println("Error loading activities: ${e.message}")
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    // Time Records (без изменений)
    fun saveTimeRecords(records: List<TimeRecord>) {
        try {
            timeRecordsFile.writeText(json.encodeToString(ListSerializer(TimeRecord.serializer()), records))
        } catch (e: Exception) {
            println("Error saving time records: ${e.message}")
        }
    }

    fun loadTimeRecords(): List<TimeRecord> {
        return if (timeRecordsFile.exists()) {
            try {
                json.decodeFromString(ListSerializer(TimeRecord.serializer()), timeRecordsFile.readText())
            } catch (e: Exception) {
                println("Error loading time records: ${e.message}")
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    fun addTimeRecord(record: TimeRecord) {
        val records = loadTimeRecords().toMutableList()
        records.add(record)
        saveTimeRecords(records)
    }

    // App Settings
    fun saveSettings(settings: AppSettings) {
        try {
            settingsFile.writeText(json.encodeToString(AppSettings.serializer(), settings))
        } catch (e: Exception) {
            println("Error saving settings: ${e.message}")
        }
    }

    fun loadSettings(): AppSettings {
        return if (settingsFile.exists()) {
            try {
                json.decodeFromString(AppSettings.serializer(), settingsFile.readText())
            } catch (e: Exception) {
                println("Error loading settings: ${e.message}")
                AppSettings.default()
            }
        } else {
            AppSettings.default()
        }
    }
}