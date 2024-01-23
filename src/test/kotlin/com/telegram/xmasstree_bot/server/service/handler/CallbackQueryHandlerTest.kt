package com.telegram.xmasstree_bot.server.service.handler

import com.telegram.xmasstree_bot.bot.XMassTreeBot
import com.telegram.xmasstree_bot.server.service.UserInteractionProcessor
import com.telegram.xmasstree_bot.server.service.strategy.CallbackMessageStrategy
import com.telegram.xmasstree_bot.server.service.strategy.StrategyFactory
import com.telegram.xmasstree_bot.server.service.strategy.StrategyType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message

@ExtendWith(MockitoExtension::class)
class CallbackQueryHandlerTest(
    @Mock private val userInteractionProcessor: UserInteractionProcessor,
    @Mock private val strategyFactory: StrategyFactory
) {

    @Mock
    private lateinit var callbackStrategy: CallbackMessageStrategy

    @Mock
    private lateinit var bot: XMassTreeBot


    @InjectMocks
    private lateinit var callbackQueryHandler: CallbackQueryHandler

    @Test
    fun handle() {
        val botApiObject: BotApiObject = Message()
        val response = SendMessage()

        Mockito.`when`(strategyFactory.getStrategy(StrategyType.CALLBACK)).thenReturn(callbackStrategy)
        Mockito.doNothing().`when`(userInteractionProcessor).setStrategy(callbackStrategy)
        Mockito.`when`(userInteractionProcessor.processMessage(botApiObject, bot)).thenReturn(response)

        val result = callbackQueryHandler.handle(botApiObject, bot)

        assertEquals(response, result)
    }

}