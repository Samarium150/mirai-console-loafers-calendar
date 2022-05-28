import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.6.21"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.11.1"
    id("com.geoffgranum.gradle-conventional-changelog") version "+"
}

group = "io.github.samarium150"
version = "1.6.0"

repositories {
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
}

dependencies {
    implementation("cn.hutool:hutool-cron:5.8.2")
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
