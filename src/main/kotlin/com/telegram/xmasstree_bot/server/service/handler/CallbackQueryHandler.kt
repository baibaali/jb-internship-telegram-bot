package com.telegram.xmasstree_bot.server.service.handler

import com.telegram.xmasstree_bot.bot.XMassTreeBot
import com.telegram.xmasstree_bot.server.entity.User
import com.telegram.xmasstree_bot.server.service.UserInteractionProcessor
import com.telegram.xmasstree_bot.server.service.UserService
import com.telegram.xmasstree_bot.server.service.strategy.StrategyFactory
import com.telegram.xmasstree_bot.server.service.strategy.StrategyType
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.CallbackQuery

/**
 * CallbackQueryHandler class.
 * Used to handle callback queries.
 * @property userInteractionProcessor UserInteractionProcessor used to process user interaction.
 * @property strategyFactory StrategyFactory used to get a strategy of message processing.
 */
@Service
class CallbackQueryHandler(
    private val userInteractionProcessor: UserInteractionProcessor,
    private val strategyFactory: StrategyFactory
): AbstractHandler() {
    override fun handle(botApiObject: BotApiObject, bot: XMassTreeBot): BotApiMethod<*>? {
        userInteractionProcessor.setStrategy(strategyFactory.getStrategy(StrategyType.CALLBACK))
        return userInteractionProcessor.processMessage(botApiObject, bot)
    }
}