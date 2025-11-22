package app.sw.data.model

import kotlinx.serialization.Serializable

/**
 * Модель настроек приложения.
 *
 * Хранит пользовательские настройки, которые сохраняются между сессиями.
 * Используется для включения/отключения функциональности и настройки интерфейса.
 *
 * @property isActivityTrackingEnabled Включен ли трекинг активностей
 *        - `true`: показывать выбор активностей и вести логи
 *        - `false`: базовый режим секундомера без привязки к активностям
 * @property showLogsInMainScreen Показывать ли историю логов на главном экране
 *        - `true`: отображать историю под секундомером
 *        - `false`: скрывать историю (только кнопки управления)
 * @property mainWindowWidth Ширина главного окна в dp
 * @property mainWindowHeight Высота главного окна в dp
 * @property settingsWindowWidth Ширина окна настроек в dp
 * @property settingsWindowHeight Высота окна настроек в dp
 *
 *
 * @see Activity
 * @see TimeRecord
 */
@Serializable
data class AppSettings(
    val isActivityTrackingEnabled: Boolean = false,
    val showLogsInMainScreen: Boolean = true,
    val mainWindowWidth: Int = 400,
    val mainWindowHeight: Int = 180,
    val settingsWindowWidth: Int = 500,
    val settingsWindowHeight: Int = 500,
) {
    companion object {
        /**
         * Создает настройки по умолчанию.
         *
         * Используется при первом запуске приложения или при ошибке загрузки настроек.
         *
         * @return Настройки приложения со значениями по умолчанию
         */
        fun default() = AppSettings()
    }
}