import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.6.21"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.12.0-RC"
    id("com.geoffgranum.gradle-conventional-changelog") version "+"
}

group = "io.github.samarium150"
version = "1.7.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("cn.hutool:hutool-cron:5.8.3")
}

tasks {
    withType<KotlinCompile>().all {
        kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
        kotlinOptions.jvmTarget = "11"
    }
    changelog {
        appName = project.name
        versionNum = "$version"
        repoUrl = "https://github.com/Samarium150/mirai-console-loafers-calendar"
        trackerUrl = "https://github.com/Samarium150/mirai-console-loafers-calendar/issues"
    }
}
