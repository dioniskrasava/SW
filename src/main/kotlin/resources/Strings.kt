// Файл: resources/Strings.kt
package app.sw.resources

interface StringResources {
    val app_name: String
    val start: String
    val pause: String
    val reset: String
    val empty_history: String
    val activity_tracking: String
    val settings: String

    // Добавляем все недостающие строки:
    val current_activity: String
    val choose_activity: String
    val activity_selected: String
    val all_activities_history: String
    val total_pause_time: String
    val clear_history: String
    val manage_activities: String
    val no_activities: String
    val new_activity: String
    val edit_activity: String
    val activity_name: String
    val color: String
    val enter_activity_name: String
    val cancel: String
    val add: String
    val save: String
    val delete: String
    val general_settings: String
    val activities_management: String
    val window_settings: String
    val enable_activity_tracking: String
    val activity_tracking_description: String
    val activity_tracking_enabled: String
    val window_size_settings: String
    val main_window: String
    val main_window_description: String
    val settings_window: String
    val settings_window_description: String
    val width: String
    val height: String
    val save_sizes: String
    val changes_will_apply: String
    val enter_numeric_values: String
    val language: String
    val russian: String
    val english: String
    val inactivity: String
    val idle: String

    val continue_text: String
    val complete: String
    val current_activity_text: String // для отображения "Текущая" в списке
}

object Strings {
    object Ru : StringResources {
        override val app_name = "Умный секундомер"
        override val start = "Старт"
        override val pause = "Пауза"
        override val reset = "Сброс"
        override val empty_history = "История пуста"
        override val activity_tracking = "Трекинг активностей"
        override val settings = "Настройки"
        override val current_activity = "Текущая активность:"
        override val choose_activity = "Выберите активность"
        override val activity_selected = "Активность '%s' выбрана"
        override val all_activities_history = "История всех активностей:"
        override val total_pause_time = "Общее время пауз:"
        override val clear_history = "Очистить историю"
        override val manage_activities = "Управление активностями..."
        override val no_activities = "Нет активностей"
        override val new_activity = "Новая активность"
        override val edit_activity = "Редактирование"
        override val activity_name = "Название:"
        override val color = "Цвет:"
        override val enter_activity_name = "Введите название активности"
        override val cancel = "Отмена"
        override val add = "Добавить"
        override val save = "Сохранить"
        override val delete = "Удалить"
        override val general_settings = "Основные настройки"
        override val activities_management = "Управление активностями"
        override val window_settings = "Настройки окон"
        override val enable_activity_tracking = "Включить выбор активностей"
        override val activity_tracking_description = "Позволяет выбирать активности и вести логи времени"
        override val activity_tracking_enabled = "Теперь в основном окне можно выбирать активности и просматривать логи"
        override val window_size_settings = "Настройки размеров окон"
        override val main_window = "Главное окно (секундомер)"
        override val main_window_description = "Размер окна в основном режиме работы"
        override val settings_window = "Окно настроек"
        override val settings_window_description = "Размер окна в расширенном режиме настроек"
        override val width = "Ширина (dp)"
        override val height = "Высота (dp)"
        override val save_sizes = "Сохранить размеры"
        override val changes_will_apply = "Изменения вступят в силу при следующем открытии соответствующего окна"
        override val enter_numeric_values = "Введите числовые значения"
        override val language = "Язык / Language"
        override val russian = "Русский"
        override val english = "English"
        override val inactivity = "Бездействие"
        override val idle = "Простой"

        override val continue_text = "Продолжение"
        override val complete = "Завершено"
        override val current_activity_text = "Текущая"
    }

    object En : StringResources {
        override val app_name = "Smart Stopwatch"
        override val start = "Start"
        override val pause = "Pause"
        override val reset = "Reset"
        override val empty_history = "History is empty"
        override val activity_tracking = "Activity Tracking"
        override val settings = "Settings"
        override val current_activity = "Current activity:"
        override val choose_activity = "Choose activity"
        override val activity_selected = "Activity '%s' selected"
        override val all_activities_history = "All activities history:"
        override val total_pause_time = "Total pause time:"
        override val clear_history = "Clear history"
        override val manage_activities = "Manage activities..."
        override val no_activities = "No activities"
        override val new_activity = "New activity"
        override val edit_activity = "Edit"
        override val activity_name = "Name:"
        override val color = "Color:"
        override val enter_activity_name = "Enter activity name"
        override val cancel = "Cancel"
        override val add = "Add"
        override val save = "Save"
        override val delete = "Delete"
        override val general_settings = "General Settings"
        override val activities_management = "Activities Management"
        override val window_settings = "Window Settings"
        override val enable_activity_tracking = "Enable activity selection"
        override val activity_tracking_description = "Allows to select activities and track time logs"
        override val activity_tracking_enabled = "Now you can select activities and view logs in the main window"
        override val window_size_settings = "Window size settings"
        override val main_window = "Main window (stopwatch)"
        override val main_window_description = "Window size in main working mode"
        override val settings_window = "Settings window"
        override val settings_window_description = "Window size in extended settings mode"
        override val width = "Width (dp)"
        override val height = "Height (dp)"
        override val save_sizes = "Save sizes"
        override val changes_will_apply = "Changes will apply when opening the corresponding window next time"
        override val enter_numeric_values = "Enter numeric values"
        override val language = "Language"
        override val russian = "Russian"
        override val english = "English"
        override val inactivity = "Inactivity"
        override val idle = "Idle"

        override val continue_text = "Continue"
        override val complete = "Complete"
        override val current_activity_text = "Current"
    }
}