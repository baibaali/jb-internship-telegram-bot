package com.telegram.xmasstree_bot.server.handler

import com.telegram.xmasstree_bot.bot.XMassTreeBot
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject
import org.telegram.telegrambots.meta.api.methods.BotApiMethod

@Service
class CommandHandler: AbstractHandler() {
    override fun handle(botApiObject: BotApiObject, bot: XMassTreeBot): BotApiMethod<*> {
        throw NotImplementedError()
    }
}