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
import io.github.samarium150.mirai.plugin.loafers_calendar.util.cleanCalendarCache
import io.github.samarium150.mirai.plugin.loafers_calendar.util.logger
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import java.text.ParseException

object Clean : SimpleCommand(
    MiraiConsoleLoafersCalendar,
    primaryName = "clean-calendar-cache",
    secondaryNames = CommandConfig.cleanCalendarCache,
    description = "清理日历缓存指令"
) {
    @ExperimentalCommandDescriptors
    @ConsoleExperimentalApi
    override val prefixOptional = true

    @Suppress("unused")
    @Handler
    suspend fun CommandSender.handle(date: String? = null) {
        if (PluginConfig.save) {
            cleanCalendarCache(date).onFailure {
                if (it is ParseException)
                    sendMessage("日期格式错误，请使用 yyyyMMdd 格式")
                else
                    logger.error(it)
            }.onSuccess {
                sendMessage("清理成功")
            }
        }
    }
}
