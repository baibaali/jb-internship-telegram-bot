package com.telegram.xmasstree_bot.server.service

import com.telegram.xmasstree_bot.bot.XMassTreeBot
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.CallbackQuery

interface CallbackQueryProcessor {
    fun process(callbackQuery: CallbackQuery, bot: XMassTreeBot): BotApiMethod<*>
}