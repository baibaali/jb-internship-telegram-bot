package com.telegram.xmasstree_bot.bot

import com.telegram.xmasstree_bot.config.TelegramBotProperties
import com.telegram.xmasstree_bot.server.service.UpdateDispatcher
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramWebhookBot
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Update

/**
 * This is the main bot class.
 * It handles all the user interactions.
 */
@Component
class XMassTreeBot(
    private val telegramBotProperties: TelegramBotProperties,
    private val updateDispatcher: UpdateDispatcher
): TelegramWebhookBot(telegramBotProperties.getBotToken()) {
    override fun getBotUsername(): String = telegramBotProperties.getBotName()
    override fun onWebhookUpdateReceived(update: Update): BotApiMethod<*>? {
        return updateDispatcher.dispatch(update, this)
    }
    override fun getBotPath(): String {
        return telegramBotProperties.getWebHookUrl()
    }
}