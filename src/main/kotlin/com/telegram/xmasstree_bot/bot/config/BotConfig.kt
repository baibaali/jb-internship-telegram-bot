package com.telegram.xmasstree_bot.bot.config

import com.telegram.xmasstree_bot.bot.XMassTreeBot
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

/*
 * Bot configuration.
 */
@Configuration
class BotConfig {
    @Bean
    fun telegramBotsApi(bot: XMassTreeBot): TelegramBotsApi =
        TelegramBotsApi(DefaultBotSession::class.java).apply {
            registerBot(bot)
        }
}