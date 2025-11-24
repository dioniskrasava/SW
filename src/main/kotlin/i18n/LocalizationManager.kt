// Файл: i18n/LocalizationManager.kt
package app.sw.i18n

import app.sw.data.repository.ActivityRepository
import app.sw.resources.StringResources
import app.sw.resources.Strings

object LocalizationManager {
    var currentResources: StringResources = Strings.Ru
        private set

    fun setLanguage(language: String, repository: ActivityRepository? = null) {
        currentResources = when (language) {
            "en" -> Strings.En
            "ru" -> Strings.Ru
            else -> Strings.Ru // fallback
        }

        // Сохраняем настройки языка, если передан репозиторий
        repository?.let { repo ->
            val settings = repo.loadSettings()
            repo.saveSettings(settings.copy(language = language))
        }
    }

    // Удобные функции доступа
    fun getString(resource: StringResources.() -> String): String {
        return resource(currentResources)
    }

    // Функция для строк с параметрами
    fun getString(format: String, vararg args: Any): String {
        return format.format(*args)
    }
}

// Extension для удобства
fun stringResource(block: StringResources.() -> String): String {
    return LocalizationManager.getString(block)
}