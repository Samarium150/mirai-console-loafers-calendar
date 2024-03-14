/*
 * Copyright (c) 2023 Samarium
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/agpl-3.0.txt>
 */
package io.github.samarium150.mirai.plugin.loafers_calendar.util

import io.github.samarium150.mirai.plugin.loafers_calendar.MiraiConsoleLoafersCalendar
import io.github.samarium150.mirai.plugin.loafers_calendar.config.PluginConfig
import io.github.samarium150.mirai.plugin.loafers_calendar.data.PluginData
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runInterruptible
import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.Contact.Companion.sendImage
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.imageio.ImageIO

internal val logger by lazy {
    MiraiConsoleLoafersCalendar.logger
}

internal val cacheFolder by lazy {
    val folder = MiraiConsoleLoafersCalendar.dataFolder.resolve("cache")
    if (!folder.exists()) folder.mkdirs()
    folder
}

internal val httpClient by lazy {
    MiraiConsoleLoafersCalendar.client
}

internal val CTT = TimeZone.getTimeZone("CTT")

internal val calendar get() = Calendar.getInstance(CTT)

internal val SDF = SimpleDateFormat("yyyyMMdd").apply { timeZone = CTT }

internal fun getUTC8Date(): String {
    return SDF.format(calendar.time)
}

internal fun isSunday(date: Date): Boolean {
    return calendar.apply { time = date }.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
}

internal fun Date.isSameDay(other: Date): Boolean {
    return calendar.apply {
        time = this@isSameDay
    }.get(Calendar.DAY_OF_YEAR) == calendar.apply {
        time = other
    }.get(Calendar.DAY_OF_YEAR)
}

@Throws(ParseException::class)
internal fun sanitizeDate(date: String?): String {
    if (date == null) return getUTC8Date()
    SDF.parse(date)
    return date
}

@Throws(CancellationException::class, IOException::class)
internal suspend fun convertToPNG(bytes: ByteArray): ByteArray {
    return runInterruptible(Dispatchers.IO) {
        val output = ByteArrayOutputStream()
        ImageIO.write(ImageIO.read(bytes.inputStream()), "png", output)
        return@runInterruptible output.toByteArray()
    }
}

@Throws(
    CancellationException::class,
    IOException::class,
    NotUpdatedYetException::class,
    ParseException::class,
    ServerResponseException::class
)
internal suspend fun downloadLoafersCalender(date: String? = null): InputStream {
    val target = sanitizeDate(date)
    val targetDate = SDF.parse(target)
    val file = cacheFolder.resolve("$target.png")
    if (file.exists()) return file.inputStream()
    var response: HttpResponse = httpClient.get("https://api.j4u.ink/proxy/redirect/moyu/calendar/$target.png")
    var body: ByteArray = response.body()
    if (response.etag() == "dcfa2a3538f911d47550b49cbfbfb23f" && !isSunday(targetDate)) {
        if (!targetDate.isSameDay(calendar.time))
            throw NotUpdatedYetException("API is not updated yet")
        response = httpClient.get("https://api.vvhan.com/api/moyu")
        if (response.lastModified()?.isSameDay(targetDate) == false)
            throw NotUpdatedYetException("API is not updated yet")
        body = response.body()
    }
    body = convertToPNG(body)
    if (PluginConfig.save)
        file.writeBytes(body)
    return body.inputStream()
}

internal fun Bot.sendUpdate() = MiraiConsoleLoafersCalendar.launch {
    val resource = downloadLoafersCalender().toExternalResource()
    logger.info("推送到好友")
    friends.forEach {
        if (PluginData.subscribedFriends.contains(it.id))
            it.sendImage(resource)
    }
    logger.info("推送到群")
    groups.forEach {
        if (PluginData.subscribedGroups.contains(it.id))
            it.sendImage(resource)
    }
    runInterruptible(Dispatchers.IO) {
        resource.close()
    }
}

@Throws(ParseException::class)
internal fun cleanCalendarCache(date: String?) = runCatching {
    if (date == null) {
        cacheFolder.listFiles()?.forEach {
            if (it.isFile && it.name.endsWith(".png"))
                it.delete()
        }
    } else {
        val file = cacheFolder.resolve("${sanitizeDate(date)}.png")
        if (file.isFile)
            file.delete()
        return@runCatching
    }
}
