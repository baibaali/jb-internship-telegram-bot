package com.telegram.xmasstree_bot.server.service.handler

import com.telegram.xmasstree_bot.bot.XMassTreeBot
import com.telegram.xmasstree_bot.server.service.UserInteractionProcessor
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.CallbackQuery

@Service
class CallbackQueryHandler(
    private val userInteractionProcessor: UserInteractionProcessor
): AbstractHandler() {
    override fun handle(botApiObject: BotApiObject, bot: XMassTreeBot): BotApiMethod<*>? {
        val callbackQuery = botApiObject as CallbackQuery
        return userInteractionProcessor.processCallbackQuery(callbackQuery, bot)
    }
}