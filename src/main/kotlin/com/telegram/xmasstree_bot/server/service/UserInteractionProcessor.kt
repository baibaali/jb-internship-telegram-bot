package com.telegram.xmasstree_bot.server.service

import com.telegram.xmasstree_bot.bot.XMassTreeBot
import com.telegram.xmasstree_bot.server.service.interaction.AbstractInteractionProcessor
import com.telegram.xmasstree_bot.server.service.strategy.MessageStrategy
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject
import org.telegram.telegrambots.meta.api.methods.BotApiMethod

@Service
class UserInteractionProcessor: AbstractInteractionProcessor() {

    private lateinit var strategy: MessageStrategy

    fun setStrategy(strategy: MessageStrategy) {
        this.strategy = strategy
    }

    override fun processMessage(botApiObject: BotApiObject, bot: XMassTreeBot): BotApiMethod<*>? {
        return strategy.execute(botApiObject, bot)
    }

}