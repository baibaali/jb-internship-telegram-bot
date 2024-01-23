package com.telegram.xmasstree_bot.server.service.factory

import com.telegram.xmasstree_bot.server.entity.XMassTree
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.telegram.telegrambots.meta.api.methods.send.SendLocation
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText

@ExtendWith(MockitoExtension::class)
class BotPredefinedMessageFactoryTest(
    @Mock private val messageFactory: MessageFactory,
    @Mock private val keyboardFactory: KeyboardFactory
) {

    @InjectMocks
    private lateinit var botPredefinedMessageFactory: BotPredefinedMessageFactory

    private val chatId = 1L
    private val messageId = 1

    @Test
    fun sendTooManyUploadsMessage() {
        val response = EditMessageText()
        Mockito.`when`(messageFactory.createEditMessageText(
            Mockito.anyLong(), Mockito.anyInt(), Mockito.anyString(), Mockito.any())
        ).thenReturn(response)

        val result = botPredefinedMessageFactory.sendTooManyUploadsMessage(chatId, messageId)

        assertEquals(response, result)
    }

    @Test
    fun sendSessionExpiredMessage() {
        val response = SendMessage()
        Mockito.`when`(messageFactory.createSendMessage(
            Mockito.anyLong(), Mockito.anyString(), Mockito.any())
        ).thenReturn(response)

        val result = botPredefinedMessageFactory.sendSessionExpiredMessage(chatId)

        assertEquals(response, result)
    }

    @Test
    fun returnToMenuEdit() {
        val response = EditMessageText()
        Mockito.`when`(messageFactory.createEditMessageText(
            Mockito.anyLong(), Mockito.anyInt(), Mockito.anyString(), Mockito.any())
        ).thenReturn(response)

        val result = botPredefinedMessageFactory.returnToMenuEdit(chatId, messageId)

        assertEquals(response, result)
    }

    @Test
    fun returnToMenuSend() {
        val response = SendMessage()
        Mockito.`when`(messageFactory.createSendMessage(
            Mockito.anyLong(), Mockito.anyString(), Mockito.any())
        ).thenReturn(response)

        val result = botPredefinedMessageFactory.returnToMenuSend(chatId)

        assertEquals(response, result)
    }

    @Test
    fun displayPageMessageEdit() {
        val response = EditMessageMedia()
        Mockito.`when`(messageFactory.createEditMessageMedia(
            Mockito.anyLong(), Mockito.anyInt(), Mockito.anyString(), Mockito.any())
        ).thenReturn(response)

        val result = botPredefinedMessageFactory.displayPageMessageEdit(chatId, messageId, XMassTree(),0, 0)

        assertEquals(response, result)
    }

    @Test
    fun displayPageMessageSend() {
        val response = SendPhoto()
        Mockito.`when`(messageFactory.createSendPhoto(
            Mockito.anyLong(), Mockito.anyString(), Mockito.any())
        ).thenReturn(response)

        val result = botPredefinedMessageFactory.displayPageMessageSend(chatId, XMassTree(),0, 0)

        assertEquals(response, result)
    }

    @Test
    fun sendEmptyGalleryMessage() {
        val response = EditMessageText()
        Mockito.`when`(messageFactory.createEditMessageText(
            Mockito.anyLong(), Mockito.anyInt(), Mockito.anyString(), Mockito.any())
        ).thenReturn(response)

        val result = botPredefinedMessageFactory.sendEmptyGalleryMessage(chatId, messageId)

        assertEquals(response, result)
    }

    @Test
    fun sendWaitForLocation() {
        val response = EditMessageText()
        Mockito.`when`(messageFactory.createEditMessageText(
            Mockito.anyLong(), Mockito.anyInt(), Mockito.anyString(), Mockito.any())
        ).thenReturn(response)

        val result = botPredefinedMessageFactory.sendWaitForLocation(chatId, messageId)

        assertEquals(response, result)
    }

    @Test
    fun sendWaitForImage() {
        val response = SendMessage()
        Mockito.`when`(messageFactory.createSendMessage(
            Mockito.anyLong(), Mockito.anyString(), Mockito.any())
        ).thenReturn(response)

        val result = botPredefinedMessageFactory.sendWaitForImage(chatId)

        assertEquals(response, result)
    }

    @Test
    fun sendStartMessage() {
        val response = SendMessage()
        Mockito.`when`(messageFactory.createSendMessage(
            Mockito.anyLong(), Mockito.anyString(), Mockito.any())
        ).thenReturn(response)

        val result = botPredefinedMessageFactory.sendStartMessage(chatId)

        assertEquals(response, result)
    }

    @Test
    fun sendUnknownCommandMessage() {
        val response = SendMessage()
        Mockito.`when`(messageFactory.createSendMessage(
            Mockito.anyLong(), Mockito.anyString(), Mockito.any())
        ).thenReturn(response)

        val result = botPredefinedMessageFactory.sendUnknownCommandMessage(chatId)

        assertEquals(response, result)
    }

    @Test
    fun sendOutdatedDataMessage() {
        val response = SendMessage()
        Mockito.`when`(messageFactory.createSendMessage(
            Mockito.anyLong(), Mockito.anyString(), Mockito.any())
        ).thenReturn(response)

        val result = botPredefinedMessageFactory.sendOutdatedDataMessage(chatId)

        assertEquals(response, result)
    }

    @Test
    fun sendLocationMessage() {
        val response = SendLocation()
        Mockito.`when`(messageFactory.createSendLocation(
            Mockito.anyLong(), Mockito.anyDouble(), Mockito.anyDouble(), Mockito.any())
        ).thenReturn(response)

        val result = botPredefinedMessageFactory.sendLocationMessage(chatId, 0.0, 0.0, 0, 0)

        assertEquals(response, result)
    }

    @Test
    fun sendLocationErrorMessage() {
        val response = SendMessage()
        Mockito.`when`(messageFactory.createSendMessage(
            Mockito.anyLong(), Mockito.anyString(), Mockito.any())
        ).thenReturn(response)

        val result = botPredefinedMessageFactory.sendLocationErrorMessage(chatId)

        assertEquals(response, result)
    }

    @Test
    fun sendInternalErrorMessage() {
        val response = SendMessage()
        Mockito.`when`(messageFactory.createSendMessage(
            Mockito.anyLong(), Mockito.anyString(), Mockito.any())
        ).thenReturn(response)

        val result = botPredefinedMessageFactory.sendInternalErrorMessage(chatId)

        assertEquals(response, result)
    }

    @Test
    fun sendLocationOutOfBorderMessage() {
        val response = SendMessage()
        Mockito.`when`(messageFactory.createSendMessage(
            Mockito.anyLong(), Mockito.anyString(), Mockito.any())
        ).thenReturn(response)

        val result = botPredefinedMessageFactory.sendLocationOutOfBorderMessage(chatId)

        assertEquals(response, result)
    }
}