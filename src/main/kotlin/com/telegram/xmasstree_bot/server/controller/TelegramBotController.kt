package com.telegram.xmasstree_bot.server.controller

import com.telegram.xmasstree_bot.bot.XMassTreeBot
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Update

/**
 * Controller for telegram bot webhook.
 * Receives updates from telegram and sends them to bot.
 * @param bot - telegram bot instance
 */
@RestController
class TelegramBotController(
    private val bot: XMassTreeBot
) {

    /**
     * Receives updates from telegram and sends them to bot.
     * @param update - update from telegram
     * @return Some of the subclasses of BotApiMethod or null
     */
    @PostMapping
    fun onUpdateReceived(@RequestBody update: Update): BotApiMethod<*>? {
        return bot.onWebhookUpdateReceived(update)
    }

}