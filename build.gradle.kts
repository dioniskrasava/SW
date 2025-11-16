plugins {
    kotlin("jvm") version "1.9.0"
    id("org.jetbrains.compose") version "1.5.0"
}

group = "app.sw"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(compose.desktop.currentOs)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
}

compose.desktop {
    application {
        mainClass = "app.sw.MainKt"

        nativeDistributions {
            packageName = "ComposeCounter"
            packageVersion = "1.0.0"
            description = "Простое приложение-счётчик"

            linux {
                appCategory = "Utility"
            }

            // Только AppImage для простоты
            targetFormats(
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.AppImage,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb
            )
        }
    }
}

kotlin {
    jvmToolchain(17)
}