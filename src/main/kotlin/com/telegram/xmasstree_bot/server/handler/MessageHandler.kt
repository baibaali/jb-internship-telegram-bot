package com.telegram.xmasstree_bot.server.handler

import com.telegram.xmasstree_bot.bot.XMassTreeBot
import org.jvnet.hk2.annotations.Service
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject
import org.telegram.telegrambots.meta.api.methods.BotApiMethod

@Service
class MessageHandler: AbstractHandler() {
    override fun handle(botApiObject: BotApiObject, bot: XMassTreeBot): BotApiMethod<*> {
        throw NotImplementedError()
    }
}