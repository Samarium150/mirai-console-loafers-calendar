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
import io.github.samarium150.mirai.plugin.loafers_calendar.util.NotUpdatedYetException
import io.github.samarium150.mirai.plugin.loafers_calendar.util.downloadLoafersCalender
import io.github.samarium150.mirai.plugin.loafers_calendar.util.logger
import io.ktor.client.plugins.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.contact.Contact.Companion.sendImage
import java.text.ParseException

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
    suspend fun CommandSender.handle(date: String? = null) {
        val inputStream = runCatching {
            downloadLoafersCalender(date)
        }.getOrElse {
            when (it) {
                is ParseException -> sendMessage("日期格式错误，请使用 yyyyMMdd 格式")
                is ServerResponseException -> sendMessage("获取日历图片失败")
                is NotUpdatedYetException -> sendMessage("日历图片还未更新")
                else -> logger.error(it)
            }
            return@handle
        }
        if (this is CommandSenderOnMessage<*>)
            fromEvent.subject.sendImage(inputStream)
        else if (PluginConfig.save)
            sendMessage("图片已下载")
        runInterruptible(Dispatchers.IO) {
            inputStream.close()
        }
    }
}
