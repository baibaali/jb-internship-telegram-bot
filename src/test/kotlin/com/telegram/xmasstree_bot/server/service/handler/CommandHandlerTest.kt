package com.telegram.xmasstree_bot.server.service.handler

import com.telegram.xmasstree_bot.bot.XMassTreeBot
import com.telegram.xmasstree_bot.server.service.UserInteractionProcessor
import com.telegram.xmasstree_bot.server.service.strategy.CommandMessageStrategy
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
class CommandHandlerTest(
    @Mock private val userInteractionProcessor: UserInteractionProcessor,
    @Mock private val strategyFactory: StrategyFactory
) {

        @Mock
        private lateinit var commandStrategy: CommandMessageStrategy

        @Mock
        private lateinit var bot: XMassTreeBot

        @InjectMocks
        private lateinit var commandHandler: CommandHandler

        @Test
        fun handle() {
            val botApiObject: BotApiObject = Message()
            val response = SendMessage()

            Mockito.`when`(strategyFactory.getStrategy(StrategyType.COMMAND)).thenReturn(commandStrategy)
            Mockito.doNothing().`when`(userInteractionProcessor).setStrategy(commandStrategy)
            Mockito.`when`(userInteractionProcessor.processMessage(botApiObject, bot)).thenReturn(response)

            val result = commandHandler.handle(botApiObject, bot)

            assertEquals(response, result)
        }
}