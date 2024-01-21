package com.telegram.xmasstree_bot.server.service.common

import com.telegram.xmasstree_bot.bot.XMassTreeBot
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Message

interface CommandProcessor {
    fun process(message: Message, bot: XMassTreeBot): BotApiMethod<*>
}