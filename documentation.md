# Документация проекта "Умный секундомер" (Smart Stopwatch)

## 📋 Оглавление
1. [Общее описание](#общее-описание)
2. [Архитектура проекта](#архитектура-проекта)
3. [Структура проекта](#структура-проекта)
4. [Технологический стек](#технологический-стек)
5. [Сборка и запуск](#сборка-и-запуск)
6. [Архитектурные решения](#архитектурные-решения)
7. [Ключевые компоненты](#ключевые-компоненты)
8. [Работа с данными](#работа-с-данными)
9. [Расширение функциональности](#расширение-функциональности)
10. [Известные проблемы и решения](#известные-проблемы-и-решения)

## 🎯 Общее описание

**Умный секундомер** - это десктопное приложение на Kotlin с использованием Compose Desktop, которое сочетает в себе функции классического секундомера с системой трекинга активностей. Приложение позволяет:

- ⏱️ Использовать базовый секундомер (старт/пауза/сброс)
- 🏷️ Создавать и управлять активностями (задачами, проектами)
- 📊 Автоматически логировать время по активностям
- 💾 Сохранять данные локально в JSON-файлах

## 🏗️ Архитектура проекта

Проект следует принципам **чистой архитектуры** с четким разделением ответственности:

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│     UI Layer    │ ←→ │  Domain Layer    │ ←→ │   Data Layer    │
│                 │    │ (Business Logic) │    │                 │
│ - Composable    │    │ - State Managers │    │ - Repositories  │
│ - Screens       │    │ - Use Cases      │    │ - Models        │
│ - Components    │    │ - Interfaces     │    │ - Serialization │
└─────────────────┘    └──────────────────┘    └─────────────────┘
```

## 📁 Структура проекта

```
src/main/kotlin/app/sw/
├── data/
│   ├── model/                 # Модели данных
│   │   ├── Activity.kt        # Модель активности
│   │   └── TimeRecord.kt      # Модель записи времени
│   └── repository/            # Работа с данными
│       ├── ActivityRepository.kt    # Репозиторий активностей
│       └── ListSerializer.kt        # Сериализатор списков
├── ui/
│   ├── components/            # Переиспользуемые компоненты
│   │   ├── StopwatchButton.kt # Кастомная кнопка
│   │   └── TextIcons.kt       # Текстовые иконки (fallback)
│   ├── main/                  # Основной экран
│   │   ├── StopwatchApp.kt    # Главный композабл
│   │   ├── StopwatchScreen.kt # Экран секундомера
│   │   └── StopwatchState.kt  # Состояние секундомера
│   └── settings/              # Экран настроек
│       ├── SettingsScreen.kt  # Экран настроек
│       └── ActivityEditor.kt  # Редактор активностей
├── util/                      # Вспомогательные утилиты
│   ├── TimeFormatter.kt       # Форматирование времени
│   └── ColorParser.kt         # Парсинг цветов
└── Main.kt                    # Точка входа
```

## 🔧 Технологический стек

- **Язык**: Kotlin
- **UI Framework**: Compose Desktop
- **Асинхронность**: Kotlin Coroutines
- **Сериализация**: Kotlinx Serialization (JSON)
- **Сборка**: Gradle + Kotlin DSL
- **Целевая платформа**: JVM (Java 17+)

## 🚀 Сборка и запуск

### Предварительные требования
- JDK 17 или выше
- IntelliJ IDEA (рекомендуется) или другая IDE с поддержкой Kotlin

### Сборка
```bash
./gradlew build
```

### Запуск
```bash
./gradlew run
```

### Создание дистрибутива
```bash
./gradlew package
# Или для конкретных форматов:
./gradlew packageAppImage
./gradlew packageDeb
```

## 🎨 Архитектурные решения

### 1. Управление состоянием
Используется **State Hoisting** pattern:
- Состояние поднимается наверх к ближайшему общему предку
- Stateless компоненты получают состояние через параметры
- Бизнес-логика инкапсулирована в `StopwatchState`

### 2. Навигация
Простая навигация через sealed class:
```kotlin
sealed class AppScreen {
    object Main : AppScreen()
    object Settings : AppScreen()
}
```

### 3. Работа с данными
- **Repository pattern** для абстракции доступа к данным
- Локальное хранение в JSON файлах
- Автоматическая сериализация/десериализация

## 🔑 Ключевые компоненты

### StopwatchState
**Расположение**: `ui/main/StopwatchState.kt`
**Ответственность**: Управление состоянием секундомера и логика трекинга

```kotlin
interface StopwatchState {
    val isRunning: Boolean
    val displayTime: Long
    val selectedActivityId: String?
    fun start()
    fun pause()
    fun reset()
    fun setSelectedActivity(activityId: String?)
}
```

**Особенности**:
- Автоматически сохраняет записи времени при паузе
- Поддерживает смену активности во время работы таймера
- Использует корутины для обновления времени

### ActivityRepository
**Расположение**: `data/repository/ActivityRepository.kt`
**Ответственность**: Управление сохранением данных

**Файлы данных**:
- `activities.json` - список активностей пользователя
- `time_records.json` - записи затраченного времени

### SettingsScreen
**Расположение**: `ui/settings/SettingsScreen.kt`
**Ответственность**: Управление активностями и настройками

**Функциональность**:
- Создание, редактирование, удаление активностей
- Выбор цвета для активности
- Привязка активности к текущему таймеру

## 💾 Работа с данными

### Модели данных
```kotlin
// Активность
data class Activity(
    val id: String,
    var name: String,
    val color: String, // hex
    var isActive: Boolean = true
)

// Запись времени
data class TimeRecord(
    val id: String,
    val activityId: String,
    val startTime: Long,
    val endTime: Long,
    val duration: Long
)
```

### Формат хранения
Данные сохраняются в JSON в рабочей директории приложения:
```json
// activities.json
[
  {
    "id": "123456789",
    "name": "Программирование",
    "color": "#2196F3",
    "isActive": true
  }
]

// time_records.json  
[
  {
    "id": "987654321",
    "activityId": "123456789",
    "startTime": 1700000000000,
    "endTime": 1700003600000,
    "duration": 3600000
  }
]
```

## 🛠️ Расширение функциональности

### Добавление нового экрана
1. Добавить экран в `ui/` с соответствующей директорией
2. Обновить `AppScreen` в `StopwatchApp.kt`
3. Добавить обработку навигации в `StopwatchApp.kt`

### Добавление новой модели данных
1. Создать data class в `data/model/`
2. Добавить сериализацию (`@Serializable`)
3. Обновить соответствующий Repository

### Изменение темы оформления
Цвета определяются в `Main.kt`:
```kotlin
private val DarkBackground = Color(0xFF121212)
private val PrimaryBlue = Color(0xFF64B5F6)
// и т.д.
```

## ⚠️ Известные проблемы и решения

### Проблема: Material Icons в Compose Desktop
**Симптомы**: `Unresolved reference: Icons`

**Решение**:
- Используем текстовые иконки из `TextIcons.kt`
- Или добавляем зависимость: `implementation(compose.materialIconsExtended)`

### Проблема: Парсинг цветов Android
**Симптомы**: `Unresolved reference: android`

**Решение**: Используем кастомный парсер в `ColorParser.kt`

### Проблема: Размеры окна
**Симптомы**: Окно не меняет размер при переходе между экранами

**Решение**: Используем `LaunchedEffect` и `onWindowResize` callback



