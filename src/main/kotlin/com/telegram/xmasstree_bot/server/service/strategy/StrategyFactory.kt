package com.telegram.xmasstree_bot.server.service.strategy

import org.springframework.stereotype.Service

/**
 * StrategyFactory class.
 * Used to get a strategy of message processing based on the strategy type.
 * @property locationMessageStrategy LocationMessageStrategy used to process location messages.
 * @property commandMessageStrategy CommandMessageStrategy used to process command messages.
 * @property callbackMessageStrategy CallbackMessageStrategy used to process callback messages.
 * @property textMessageStrategy TextMessageStrategy used to process text messages.
 * @property photoMessageStrategy PhotoMessageStrategy used to process photo messages.
 */
@Service
class StrategyFactory(
    private val locationMessageStrategy: LocationMessageStrategy,
    private val commandMessageStrategy: CommandMessageStrategy,
    private val callbackMessageStrategy: CallbackMessageStrategy,
    private val textMessageStrategy: TextMessageStrategy,
    private val photoMessageStrategy: PhotoMessageStrategy
) {
    /**
     * Gets a strategy of message processing based on the strategy type.
     * @param strategyType StrategyType used to get a strategy of message processing.
     * @return MessageStrategy used to process messages.
     */
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
