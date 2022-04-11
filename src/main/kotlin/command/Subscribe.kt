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
import net.mamoe.mirai.contact.MemberPermission
import net.mamoe.mirai.message.data.MessageSource.Key.quote

@Suppress("unused")
object Subscribe : SimpleCommand(
    MiraiConsoleLoafersCalendar,
    primaryName = "subscribe-loafers-calendar",
    secondaryNames = CommandConfig.subscribeCalendar,
    description = "订阅日历的更新",
) {
    @ExperimentalCommandDescriptors
    @ConsoleExperimentalApi
    override val prefixOptional = true

    @Handler
    suspend fun FriendCommandSenderOnMessage.handle() {
        if (PluginData.subscribedFriends.add(fromEvent.sender.id))
            sendMessage(fromEvent.source.quote() + "已订阅日历更新推送")
        else
            sendMessage(fromEvent.source.quote() + "已经订阅过了")
    }

    @Handler
    suspend fun MemberCommandSenderOnMessage.handle() {
        val sender = fromEvent.sender
        if (sender.permission == MemberPermission.OWNER && PluginData.subscribedGroups.add(fromEvent.subject.id))
            sendMessage(fromEvent.source.quote() + "已订阅日历更新推送")
        else if (sender.permission != MemberPermission.OWNER)
            sendMessage(fromEvent.source.quote() + "只有群主才能订阅日历更新推送")
        else
            sendMessage(fromEvent.source.quote() + "已经订阅过了")
    }
}
