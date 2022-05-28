/*
 * Copyright (c) 2022 Samarium
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
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runInterruptible
import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.Contact.Companion.sendImage
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import java.io.InputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

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

internal fun getUTC8Date(): Date {
    return Calendar.getInstance(TimeZone.getTimeZone("UTC+8")).time
}

@Throws(ParseException::class)
internal fun sanitizeDate(date: String?): String {
    if (date == null) return SimpleDateFormat("yyyyMMdd").format(getUTC8Date())
    SimpleDateFormat("yyyyMMdd").parse(date)
    return date
}

internal fun isSunday(date: String): Boolean {
    Calendar.getInstance().apply {
        time = SimpleDateFormat("yyyyMMdd").parse(date)
        return get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
    }
}

@Throws(ParseException::class, ServerResponseException::class, NotYetUpdatedException::class)
internal suspend fun downloadLoafersCalender(date: String? = null): InputStream {
    val target = sanitizeDate(date)
    val file = cacheFolder.resolve("${target}.png")
    if (file.exists()) return file.inputStream()
    val response: HttpResponse = httpClient.get("https://api.j4u.ink/proxy/redirect/moyu/calendar/${target}.png")
    if (!isSunday(target) && response.headers["etag"] == "\"6251bbbb-d2781\"")
        throw NotYetUpdatedException("API is not updated yet")
    val body: ByteArray = response.receive()
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
        else
            return@runCatching
    }
}
