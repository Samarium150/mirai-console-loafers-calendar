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
import io.github.samarium150.mirai.plugin.loafers_calendar.data.PluginData
import net.mamoe.mirai.console.command.FriendCommandSenderOnMessage
import net.mamoe.mirai.console.command.MemberCommandSenderOnMessage
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.contact.isOperator
import net.mamoe.mirai.message.data.MessageSource.Key.quote

object Unsubscribe : SimpleCommand(
    MiraiConsoleLoafersCalendar,
    primaryName = "unsubscribe-loafers-calendar",
    secondaryNames = CommandConfig.unsubscribeCalendar,
    description = "取消订阅日历的更新",
) {
    @ExperimentalCommandDescriptors
    @ConsoleExperimentalApi
    override val prefixOptional = true

    @Handler
    suspend fun FriendCommandSenderOnMessage.handle() {
        val quote = fromEvent.source.quote()
        if (PluginData.subscribedFriends.remove(fromEvent.sender.id))
            sendMessage(quote + "取消订阅成功")
        else
            sendMessage(quote + "你没有订阅过日历更新推送")
    }

    @Handler
    suspend fun MemberCommandSenderOnMessage.handle() {
        val sender = fromEvent.sender
        val quote = fromEvent.source.quote()
        val isOperator = sender.isOperator()
        if (isOperator && PluginData.subscribedGroups.remove(fromEvent.subject.id))
            sendMessage(quote + "取消订阅成功")
        else if (isOperator)
            sendMessage(quote + "只有群主和管理员才能取消订阅")
        else
            sendMessage(quote + "本群没有订阅过日历更新推送")
    }
}
