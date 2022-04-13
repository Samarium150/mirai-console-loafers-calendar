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
package io.github.samarium150.mirai.plugin.loafers_calendar.config

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object CommandConfig : AutoSavePluginConfig("CommandConfig") {
    @ValueDescription("获取摸鱼人日历指令的别名")
    val getLoafersCalendar by value(arrayOf("lc", "摸鱼人日历"))

    @ValueDescription("订阅摸鱼人日历指令的别名")
    val subscribeCalendar by value(arrayOf("slc", "订阅日历"))

    @ValueDescription("取消订阅摸鱼人日历指令的别名")
    val unsubscribeCalendar by value(arrayOf("ulc", "取消订阅日历"))

    @ValueDescription("清理日历缓存指令的别名")
    val cleanCalendarCache by value(arrayOf("ccc", "清理日历缓存"))
}
