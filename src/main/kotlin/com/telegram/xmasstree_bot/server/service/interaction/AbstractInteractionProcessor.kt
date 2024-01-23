package com.telegram.xmasstree_bot.server.service.interaction

import com.telegram.xmasstree_bot.bot.XMassTreeBot
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject
import org.telegram.telegrambots.meta.api.methods.BotApiMethod

abstract class AbstractInteractionProcessor {
    abstract fun processMessage(botApiObject: BotApiObject, bot: XMassTreeBot): BotApiMethod<*>?
}