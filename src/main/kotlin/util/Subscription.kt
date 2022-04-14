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

import net.mamoe.mirai.Bot
import org.quartz.*

internal class Subscription : Job {

    companion object {
        @JvmStatic
        fun init(): JobDetail {
            return JobBuilder.newJob(Subscription::class.java)
                .storeDurably(true)
                .withIdentity("subscription", "mirai-console-loafers-calendar")
                .build()
        }
    }

    @Throws(JobExecutionException::class)
    override fun execute(context: JobExecutionContext) {
        logger.info("推送日历订阅更新")
        Bot.instances.filter {
            it.isOnline
        }.forEach {
            it.sendUpdate()
        }
    }
}
