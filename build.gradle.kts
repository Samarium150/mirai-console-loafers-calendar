import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.8.0"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.14.0"
    id("com.geoffgranum.gradle-conventional-changelog") version "+"
}

group = "io.github.samarium150"
version = "1.8.2"

repositories {
    mavenCentral()
}

dependencies {
    implementation("cn.hutool:hutool-cron:5.8.15")
    implementation("com.twelvemonkeys.imageio:imageio-webp:3.9.4")
    implementation(platform("io.ktor:ktor-bom:2.2.3"))
    implementation("io.ktor:ktor-client-okhttp-jvm")
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
