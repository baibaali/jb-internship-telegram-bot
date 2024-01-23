package com.telegram.xmasstree_bot.server.service

import com.telegram.xmasstree_bot.bot.XMassTreeBot
import com.telegram.xmasstree_bot.server.service.handler.CallbackQueryHandler
import com.telegram.xmasstree_bot.server.service.handler.CommandHandler
import com.telegram.xmasstree_bot.server.service.handler.MessageHandler
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.Location
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.PhotoSize
import org.telegram.telegrambots.meta.api.objects.Update

@ExtendWith(MockitoExtension::class)
class UpdateDispatcherTest(
    @Mock private val messageHandler: MessageHandler,
    @Mock private val commandHandler: CommandHandler,
    @Mock private val callbackQueryHandler: CallbackQueryHandler
) {

    @Mock
    private lateinit var bot: XMassTreeBot

    @InjectMocks
    private lateinit var updateDispatcher: UpdateDispatcher

    @Test
    fun dispatchNoMessageNoLocation() {
        val update = Update()
        update.message = null
        update.callbackQuery = null

        val result = updateDispatcher.dispatch(update, bot)

        assertNull(result)
    }

    @Test
    fun dispatchCallbackQuery() {
        val update = Update()
        update.message = null
        update.callbackQuery = CallbackQuery()

        val sendMessage = SendMessage()

        Mockito.`when`(callbackQueryHandler.handle(update.callbackQuery, bot)).thenReturn(sendMessage)

        val result = updateDispatcher.dispatch(update, bot)

        assertEquals(sendMessage, result)
    }

    @Test
    fun dispatchMessageTextNoCommand() {
        val update = Update()
        update.message = Message()
        update.message.text = "text"
        update.message.location = null
        update.message.photo = null
        update.callbackQuery = null

        val sendMessage = SendMessage()

        Mockito.`when`(messageHandler.handle(update.message, bot)).thenReturn(sendMessage)

        val result = updateDispatcher.dispatch(update, bot)

        assertEquals(sendMessage, result)
    }

    @Test
    fun dispatchMessageTextCommand() {
        val update = Update()
        update.message = Message()
        update.message.text = "/start"
        update.message.location = null
        update.message.photo = null
        update.callbackQuery = null

        val sendMessage = SendMessage()

        Mockito.`when`(commandHandler.handle(update.message, bot)).thenReturn(sendMessage)

        val result = updateDispatcher.dispatch(update, bot)

        assertEquals(sendMessage, result)
    }

    @Test
    fun dispatchMessageLocation() {
        val update = Update()
        update.message = Message()
        update.message.text = null
        update.message.location = Location()
        update.message.photo = null
        update.callbackQuery = null

        val sendMessage = SendMessage()

        Mockito.`when`(messageHandler.handle(update.message, bot)).thenReturn(sendMessage)

        val result = updateDispatcher.dispatch(update, bot)

        assertEquals(sendMessage, result)
    }

    @Test
    fun dispatchMessagePhoto() {
        val update = Update()
        update.message = Message()
        update.message.text = null
        update.message.location = null
        update.message.photo = listOf(PhotoSize())
        update.callbackQuery = null

        val sendMessage = SendMessage()

        Mockito.`when`(messageHandler.handle(update.message, bot)).thenReturn(sendMessage)

        val result = updateDispatcher.dispatch(update, bot)

        assertEquals(sendMessage, result)
    }
}