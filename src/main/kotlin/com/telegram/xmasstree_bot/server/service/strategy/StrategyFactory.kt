package com.telegram.xmasstree_bot.server.service.strategy

import org.springframework.stereotype.Service

@Service
class StrategyFactory(
    private val locationMessageStrategy: LocationMessageStrategy,
    private val commandMessageStrategy: CommandMessageStrategy,
    private val callbackMessageStrategy: CallbackMessageStrategy,
    private val textMessageStrategy: TextMessageStrategy,
    private val photoMessageStrategy: PhotoMessageStrategy
) {
    fun getStrategy(strategyType: StrategyType): MessageStrategy {
        return when (strategyType) {
            StrategyType.TEXT -> textMessageStrategy
            StrategyType.PHOTO -> photoMessageStrategy
            StrategyType.LOCATION -> locationMessageStrategy
            StrategyType.COMMAND -> commandMessageStrategy
            StrategyType.CALLBACK -> callbackMessageStrategy
        }
    }
}
