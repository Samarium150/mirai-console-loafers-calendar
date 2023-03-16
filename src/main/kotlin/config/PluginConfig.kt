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

object PluginConfig : AutoSavePluginConfig("PluginConfig") {
    @ValueDescription("获取图片时调用的api\n默认使用 https://j4u.ink/moyuya/\n遇到问题请尝试更换为 https://api.vvhan.com/api/moyu")
    val api: String by value("https://j4u.ink/moyuya/")
    
    @ValueDescription("是否保存图片")
    val save: Boolean by value(true)

    @ValueDescription("Cron表达式")
    val cron: String by value("0 0 12 ? * MON-SAT *")

    @ValueDescription("执行Cron表达式的时区")
    val timezone: String by value("GMT+08:00")
}
