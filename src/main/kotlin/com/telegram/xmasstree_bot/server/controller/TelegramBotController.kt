package com.telegram.xmasstree_bot.server.controller

import com.telegram.xmasstree_bot.bot.XMassTreeBot
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Update

@RestController
class TelegramBotController(
    private val bot: XMassTreeBot
) {

    @PostMapping
    fun onUpdateReceived(@RequestBody update: Update): BotApiMethod<*>? {
        return bot.onWebhookUpdateReceived(update)
    }

}