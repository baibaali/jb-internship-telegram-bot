package com.telegram.xmasstree_bot.server.service.common

import com.telegram.xmasstree_bot.bot.XMassTreeBot
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Message

interface MessageProcessor {
    fun processText(message: Message, bot: XMassTreeBot): BotApiMethod<*>?
    fun processPhoto(message: Message, bot: XMassTreeBot): BotApiMethod<*>?
    fun processLocation(message: Message, bot: XMassTreeBot): BotApiMethod<*>?
}