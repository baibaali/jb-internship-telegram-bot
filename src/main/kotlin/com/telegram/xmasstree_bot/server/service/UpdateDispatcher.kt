package com.telegram.xmasstree_bot.server.service

import com.telegram.xmasstree_bot.bot.XMassTreeBot
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Update

@Service
class UpdateDispatcher {
    fun dispatch(update: Update, bot: XMassTreeBot): BotApiMethod<*> {
        throw NotImplementedError()
    }
}