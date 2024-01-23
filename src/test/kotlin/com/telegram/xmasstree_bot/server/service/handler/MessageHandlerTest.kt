package com.telegram.xmasstree_bot.server.service.handler

import com.telegram.xmasstree_bot.bot.XMassTreeBot
import com.telegram.xmasstree_bot.server.service.UserInteractionProcessor
import com.telegram.xmasstree_bot.server.service.strategy.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Location
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.PhotoSize

@ExtendWith(MockitoExtension::class)
class MessageHandlerTest(
    @Mock private val userInteractionProcessor: UserInteractionProcessor,
    @Mock private val strategyFactory: StrategyFactory
) {

    @Mock
    private lateinit var locationMessageStrategy: LocationMessageStrategy
    @Mock
    private lateinit var photoMessageStrategy: PhotoMessageStrategy
    @Mock
    private lateinit var textMessageStrategy: TextMessageStrategy

    @InjectMocks
    private lateinit var messageHandler: MessageHandler

    @Mock
    private lateinit var bot: XMassTreeBot

    @Test
    fun handleTextMessage() {
        val message = Message()
        message.text = "text"
        message.location = null
        message.photo = null

        val botApiObject: BotApiObject = message
        val response = SendMessage()

        Mockito.`when`(strategyFactory.getStrategy(StrategyType.TEXT)).thenReturn(textMessageStrategy)
        Mockito.doNothing().`when`(userInteractionProcessor).setStrategy(textMessageStrategy)
        Mockito.`when`(userInteractionProcessor.processMessage(botApiObject, bot)).thenReturn(response)

        val result = messageHandler.handle(botApiObject, bot)

        assertEquals(response, result)
    }

    @Test
    fun handleLocationMessage() {
        val message = Message()
        message.text = null
        message.location = Location()
        message.photo = null

        val botApiObject: BotApiObject = message
        val response = SendMessage()

        Mockito.`when`(strategyFactory.getStrategy(StrategyType.LOCATION)).thenReturn(locationMessageStrategy)
        Mockito.doNothing().`when`(userInteractionProcessor).setStrategy(locationMessageStrategy)
        Mockito.`when`(userInteractionProcessor.processMessage(botApiObject, bot)).thenReturn(response)

        val result = messageHandler.handle(botApiObject, bot)

        assertEquals(response, result)
    }

    @Test
    fun handlePhotoMessage() {
        val message = Message()
        message.text = null
        message.location = null
        message.photo = listOf(PhotoSize())

        val botApiObject: BotApiObject = message
        val response = SendMessage()

        Mockito.`when`(strategyFactory.getStrategy(StrategyType.PHOTO)).thenReturn(photoMessageStrategy)
        Mockito.doNothing().`when`(userInteractionProcessor).setStrategy(photoMessageStrategy)
        Mockito.`when`(userInteractionProcessor.processMessage(botApiObject, bot)).thenReturn(response)

        val result = messageHandler.handle(botApiObject, bot)

        assertEquals(response, result)
    }

    @Test
    fun handleUnknownMessage() {
        val message = Message()
        message.text = null
        message.location = null
        message.photo = null

        val botApiObject: BotApiObject = message

        val result = messageHandler.handle(botApiObject, bot)

        assertNull(result)
    }
}