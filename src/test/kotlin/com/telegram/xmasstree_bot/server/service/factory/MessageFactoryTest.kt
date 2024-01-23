package com.telegram.xmasstree_bot.server.service.factory

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.junit.jupiter.MockitoExtension
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard

@ExtendWith(MockitoExtension::class)
class MessageFactoryTest {

    @InjectMocks
    private lateinit var messageFactory: MessageFactory

    @Test
    fun createSendMessage() {
        val chatId = 123L
        val text = "Hello, World!"
        val replyMarkup: ReplyKeyboard? = null

        val sendMessage = messageFactory.createSendMessage(chatId, text, replyMarkup)

        assertNotNull(sendMessage)
        assertEquals(chatId.toString(), sendMessage.chatId)
        assertEquals(text, sendMessage.text)
        assertEquals(replyMarkup, sendMessage.replyMarkup)
    }

    @Test
    fun createSendLocation() {
        val chatId = 123L
        val latitude = 1.0
        val longitude = 2.0
        val replyMarkup: ReplyKeyboard? = null

        val sendLocation = messageFactory.createSendLocation(chatId, latitude, longitude, replyMarkup)

        assertNotNull(sendLocation)
        assertEquals(chatId.toString(), sendLocation.chatId)
        assertEquals(latitude, sendLocation.latitude)
        assertEquals(longitude, sendLocation.longitude)
        assertEquals(replyMarkup, sendLocation.replyMarkup)
    }

    @Test
    fun createSendPhoto() {
        val chatId = 123L
        val imageFileId = "imageFileId"
        val replyMarkup: ReplyKeyboard? = null

        val sendPhoto = messageFactory.createSendPhoto(chatId, imageFileId, replyMarkup)

        assertNotNull(sendPhoto)
        assertEquals(chatId.toString(), sendPhoto.chatId)
        assertEquals(replyMarkup, sendPhoto.replyMarkup)
    }

    @Test
    fun createEditMessageText() {
        val chatId = 123L
        val messageId = 1
        val text = "Hello, World!"
        val replyMarkup: InlineKeyboardMarkup? = null

        val editMessageText = messageFactory.createEditMessageText(chatId, messageId, text, replyMarkup)

        assertNotNull(editMessageText)
        assertEquals(chatId.toString(), editMessageText.chatId)
        assertEquals(messageId, editMessageText.messageId)
        assertEquals(text, editMessageText.text)
        assertEquals(replyMarkup, editMessageText.replyMarkup)
    }

    @Test
    fun createEditMessageMedia() {
        val chatId = 123L
        val messageId = 1
        val imageFileId = "imageFileId"
        val replyMarkup: InlineKeyboardMarkup? = null

        val editMessageMedia = messageFactory.createEditMessageMedia(chatId, messageId, imageFileId, replyMarkup)

        assertNotNull(editMessageMedia)
        assertEquals(chatId.toString(), editMessageMedia.chatId)
        assertEquals(messageId, editMessageMedia.messageId)
        assertEquals(replyMarkup, editMessageMedia.replyMarkup)
    }
}