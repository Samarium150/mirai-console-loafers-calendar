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
package io.github.samarium150.mirai.plugin.loafers_calendar.command

import io.github.samarium150.mirai.plugin.loafers_calendar.MiraiConsoleLoafersCalendar
import io.github.samarium150.mirai.plugin.loafers_calendar.config.CommandConfig
import io.github.samarium150.mirai.plugin.loafers_calendar.config.PluginConfig
import io.github.samarium150.mirai.plugin.loafers_calendar.util.cacheFolder
import io.github.samarium150.mirai.plugin.loafers_calendar.util.downloadLoafersCalender
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.contact.Contact.Companion.sendImage

@Suppress("unused")
object GetLoafersCalendar : SimpleCommand(
    MiraiConsoleLoafersCalendar,
    primaryName = "loafers-calendar",
    secondaryNames = CommandConfig.getLoafersCalendar,
    description = "获取摸鱼人日历指令"
) {

    @ExperimentalCommandDescriptors
    @ConsoleExperimentalApi
    override val prefixOptional = true

    @Handler
    suspend fun CommandSender.handle() {
        val inputStream = downloadLoafersCalender()
        if (this is CommandSenderOnMessage<*>) {
            fromEvent.subject.sendImage(inputStream)
            withContext(Dispatchers.IO) {
                inputStream.close()
            }
        } else if (PluginConfig.save)
            sendMessage("图片已下载")
    }

    @Handler
    suspend fun CommandSenderOnMessage<*>.handle(date: String) {
        if (PluginConfig.save) {
            if (date.matches(Regex("^\\d{4}-\\d{2}-\\d{2}$"))) {
                val file = cacheFolder.resolve("${date}.png")
                if (file.exists()) fromEvent.subject.sendImage(file)
                else sendMessage("没有找${date}的日历图片")
            } else sendMessage("日期格式错误，请使用 yyyy-MM-dd 格式")
        } else sendMessage("仅支持获取保存过的日历图片，请先设置PluginConfig.save为true")
    }
}
