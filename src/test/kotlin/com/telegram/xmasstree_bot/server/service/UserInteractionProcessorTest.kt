package com.telegram.xmasstree_bot.server.service

import com.telegram.xmasstree_bot.bot.XMassTreeBot
import com.telegram.xmasstree_bot.server.service.strategy.CommandMessageStrategy
import com.telegram.xmasstree_bot.server.service.strategy.MessageStrategy
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
class UserInteractionProcessorTest {

    @Mock
    private lateinit var strategy: MessageStrategy
    @Mock
    private lateinit var bot: XMassTreeBot

    @InjectMocks
    private lateinit var userInteractionProcessor: UserInteractionProcessor

    @Test
    fun processMessage() {
        val botApiObject: BotApiObject = Message()
        val sendMessage = SendMessage()

        Mockito.`when`(strategy.execute(botApiObject, bot)).thenReturn(sendMessage)

        val result = userInteractionProcessor.processMessage(botApiObject, bot)

        assertEquals(sendMessage, result)
    }
}