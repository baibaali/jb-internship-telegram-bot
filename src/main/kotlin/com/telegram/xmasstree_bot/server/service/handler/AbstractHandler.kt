package com.telegram.xmasstree_bot.server.service.handler

import com.telegram.xmasstree_bot.bot.XMassTreeBot
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject
import org.telegram.telegrambots.meta.api.methods.BotApiMethod

/**
 * AbstractHandler class.
 */
abstract class AbstractHandler {
    /**
     * Handles a given BotApiObject.
     * @param botApiObject BotApiObject to handle.
     * @param bot Bot.
     * @return BotApiMethod to send.
     */
    abstract fun handle(botApiObject: BotApiObject, bot: XMassTreeBot): BotApiMethod<*>?
}