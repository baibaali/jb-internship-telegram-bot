package com.telegram.xmasstree_bot.server.service.factory

import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendLocation
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.media.InputMedia
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard

@Component
class MessageFactory: AbstractFactory() {

    fun createSendMessage(chatId: Long, text: String, replyMarkup: ReplyKeyboard? = null): SendMessage {
        return SendMessage.builder()
            .chatId(chatId.toString())
            .replyMarkup(replyMarkup)
            .text(text)
            .build()
    }

    fun createSendLocation(chatId: Long, latitude: Double, longitude: Double, replyMarkup: ReplyKeyboard?): SendLocation {
        return SendLocation.builder()
            .chatId(chatId.toString())
            .latitude(latitude)
            .longitude(longitude)
            .replyMarkup(replyMarkup)
            .build()
    }

    fun createSendPhoto(chatId: Long, imageFileId: String, replyMarkup: ReplyKeyboard? = null): SendPhoto {
        return SendPhoto.builder()
            .chatId(chatId.toString())
            .photo(InputFile(imageFileId))
            .replyMarkup(replyMarkup)
            .build()
    }

    fun createEditMessageText(chatId: Long, messageId: Int, text: String, replyMarkup: InlineKeyboardMarkup? = null): EditMessageText {
        return EditMessageText.builder()
            .chatId(chatId.toString())
            .messageId(messageId)
            .text(text)
            .replyMarkup(replyMarkup)
            .build()
    }

    fun createEditMessageMedia(chatId: Long, messageId: Int, imageFileId: String, replyMarkup: InlineKeyboardMarkup? = null): EditMessageMedia {
        return EditMessageMedia.builder()
            .chatId(chatId.toString())
            .messageId(messageId)
            .media(InputMediaPhoto(imageFileId))
            .replyMarkup(replyMarkup)
            .build()
    }

}