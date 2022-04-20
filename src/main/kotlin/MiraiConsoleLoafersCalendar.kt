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
package io.github.samarium150.mirai.plugin.loafers_calendar

import io.github.samarium150.mirai.plugin.loafers_calendar.command.Clean
import io.github.samarium150.mirai.plugin.loafers_calendar.command.GetLoafersCalendar
import io.github.samarium150.mirai.plugin.loafers_calendar.command.Subscribe
import io.github.samarium150.mirai.plugin.loafers_calendar.command.Unsubscribe
import io.github.samarium150.mirai.plugin.loafers_calendar.config.CommandConfig
import io.github.samarium150.mirai.plugin.loafers_calendar.config.PluginConfig
import io.github.samarium150.mirai.plugin.loafers_calendar.data.PluginData
import io.github.samarium150.mirai.plugin.loafers_calendar.util.Subscription
import io.github.samarium150.mirai.plugin.loafers_calendar.util.getScheduler
import io.ktor.client.*
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import org.quartz.CronScheduleBuilder
import org.quartz.TriggerBuilder
import java.util.*

object MiraiConsoleLoafersCalendar : KotlinPlugin(
    JvmPluginDescription(
        id = "io.github.samarium150.mirai.plugin.mirai-console-loafers-calendar",
        name = "Loafers' Calender",
        version = "1.5.0",
    ) {
        author("Samarium")
    }
) {

    internal val client = HttpClient()
    private val scheduler = getScheduler()

    private fun setupQuartz(schedule: String, timezone: String) {
        val jobDetail = Subscription.init()
        val trigger = TriggerBuilder.newTrigger()
            .withIdentity("subscription", "mirai-console-loafers-calendar")
            .startNow()
            .withSchedule(
                CronScheduleBuilder
                    .cronSchedule(schedule)
                    .inTimeZone(TimeZone.getTimeZone(timezone))
            )
            .forJob(jobDetail)
            .build()
        scheduler.scheduleJob(jobDetail, trigger)
    }

    override fun onEnable() {

        PluginConfig.reload()
        CommandConfig.reload()

        PluginData.reload()

        GetLoafersCalendar.register()
        Subscribe.register()
        Unsubscribe.register()
        Clean.register()

        setupQuartz(PluginConfig.cron, PluginConfig.timezone)
        scheduler.start()

        logger.info("Plugin loaded")
    }

    override fun onDisable() {

        scheduler.shutdown(true)

        GetLoafersCalendar.unregister()
        Subscribe.unregister()
        Unsubscribe.unregister()

        client.close()

        logger.info("Plugin unloaded")
    }
}
