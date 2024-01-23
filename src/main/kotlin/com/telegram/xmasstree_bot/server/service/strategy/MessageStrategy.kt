package com.telegram.xmasstree_bot.server.service.strategy

import com.telegram.xmasstree_bot.bot.XMassTreeBot
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject
import org.telegram.telegrambots.meta.api.methods.BotApiMethod

/**
 * MessageStrategy interface.
 * Used to process messages in different ways.
 */
interface MessageStrategy {
    /**
     * Executes a strategy.
     * @param botApiObject BotApiObject to be processed.
     * @param bot XMassTreeBot used to process BotApiObject.
     * @return BotApiMethod<?> to be sent to the user.
     */
    fun execute(botApiObject: BotApiObject, bot: XMassTreeBot): BotApiMethod<*>?
}