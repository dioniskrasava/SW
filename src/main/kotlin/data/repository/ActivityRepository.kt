package app.sw.data.repository

import app.sw.data.model.Activity
import app.sw.data.model.AppSettings
import app.sw.data.model.RecordType
import app.sw.data.model.TimeRecord
import kotlinx.serialization.json.Json
import java.io.File

/**
 * Репозиторий для управления данными приложения.
 *
 * Обеспечивает сохранение, загрузку и управление всеми данными приложения:
 * - Активности (задачи)
 * - Временные записи (история работы)
 * - Настройки приложения
 *
 * Использует JSON-сериализацию для хранения данных в файлах.
 * Все операции защищены обработкой исключений для предотвращения краха приложения.
 *
 * @property activitiesFile Файл для хранения списка активностей
 * @property timeRecordsFile Файл для хранения временных записей
 * @property settingsFile Файл для хранения настроек приложения
 * @property json JSON-парсер с красивым форматированием
 *
 * @see Activity
 * @see TimeRecord
 * @see AppSettings
 * @see ListSerializer
 */
class ActivityRepository {
    private val activitiesFile = File("activities.json")
    private val timeRecordsFile = File("time_records.json")
    private val settingsFile = File("app_settings.json")
    private val json = Json { prettyPrint = true }

    /**
     * Сохраняет список активностей в файл.
     *
     * @param activities Список активностей для сохранения
     * @throws Exception В случае ошибок ввода-вывода или сериализации
     *
     * @sample ActivityRepository.loadActivities
     */
    fun saveActivities(activities: List<Activity>) {
        try {
            activitiesFile.writeText(json.encodeToString(ListSerializer(Activity.serializer()), activities))
        } catch (e: Exception) {
            println("Error saving activities: ${e.message}")
        }
    }

    /**
     * Загружает список активностей из файла.
     *
     * @return Список активностей или пустой список, если файл не существует или произошла ошибка
     *
     * @sample ActivityRepository.saveActivities
     */
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

    /**
     * Сохраняет список временных записей в файл.
     *
     * @param records Список временных записей для сохранения
     *
     * @see TimeRecord
     * @see loadTimeRecords
     */
    fun saveTimeRecords(records: List<TimeRecord>) {
        try {
            timeRecordsFile.writeText(json.encodeToString(ListSerializer(TimeRecord.serializer()), records))
        } catch (e: Exception) {
            println("Error saving time records: ${e.message}")
        }
    }

    /**
     * Загружает список временных записей из файла.
     *
     * @return Список временных записей, отсортированный по времени начала
     *
     * @see TimeRecord
     * @see saveTimeRecords
     */
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

    /**
     * Добавляет новую временную запись в историю.
     *
     * Загружает текущие записи, добавляет новую и сохраняет обновленный список.
     *
     * @param record Новая временная запись для добавления
     *
     * @see TimeRecord
     * @see saveTimeRecords
     * @see loadTimeRecords
     */
    fun addTimeRecord(record: TimeRecord) {
        val records = loadTimeRecords().toMutableList()
        records.add(record)
        saveTimeRecords(records)
    }

    /**
     * Сохраняет настройки приложения в файл.
     *
     * @param settings Настройки для сохранения
     *
     * @see AppSettings
     * @see loadSettings
     */
    fun saveSettings(settings: AppSettings) {
        try {
            settingsFile.writeText(json.encodeToString(AppSettings.serializer(), settings))
        } catch (e: Exception) {
            println("Error saving settings: ${e.message}")
        }
    }

    /**
     * Загружает настройки приложения из файла.
     *
     * Если файл не существует или произошла ошибка, возвращает настройки по умолчанию.
     *
     * @return Загруженные настройки или настройки по умолчанию
     *
     * @see AppSettings
     * @see AppSettings.default
     * @see saveSettings
     */
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

    /**
     * Возвращает историю временных записей, отсортированную по времени.
     *
     * Записи сортируются по убыванию времени начала (самые новые первыми).
     *
     * @return Отсортированный список временных записей
     *
     * @see TimeRecord
     * @see loadTimeRecords
     */
    fun getActivityLogs(): List<TimeRecord> {
        return loadTimeRecords().sortedByDescending { it.startTime }
    }

    /**
     * Очищает всю историю временных записей.
     *
     * Удаляет файл с временными записями. Используется с осторожностью,
     * так как операция необратима.
     *
     * @see timeRecordsFile
     * @see saveTimeRecords
     */
    fun clearLogs() {
        try {
            timeRecordsFile.delete()
        } catch (e: Exception) {
            println("Error clearing logs: ${e.message}")
        }
    }

    /**
     * Возвращает записи о периодах бездействия.
     *
     * @return Список временных записей типа [RecordType.INACTIVE]
     *
     * @see TimeRecord
     * @see RecordType.INACTIVE
     * @see loadTimeRecords
     */
    fun getInactiveTimeRecords(): List<TimeRecord> {
        return loadTimeRecords().filter { it.type == RecordType.INACTIVE }
    }
}