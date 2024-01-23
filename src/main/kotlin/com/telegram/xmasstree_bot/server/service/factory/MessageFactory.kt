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

/**
 * MessageFactory class. Used to create messages.
 */
@Component
class MessageFactory: AbstractFactory() {

    /**
     * Creates SendMessage object with a given configuration.
     * @param chatId Chat id.
     * @param text Message text.
     * @param replyMarkup Reply keyboard.
     * @return SendMessage object.
     */
    fun createSendMessage(chatId: Long, text: String, replyMarkup: ReplyKeyboard? = null): SendMessage {
        return SendMessage.builder()
            .chatId(chatId.toString())
            .replyMarkup(replyMarkup)
            .text(text)
            .build()
    }

    /**
     * Creates SendLocation object with a given configuration.
     * @param chatId Chat id.
     * @param latitude Latitude.
     * @param longitude Longitude.
     * @param replyMarkup Reply keyboard.
     * @return SendLocation object.
     */
    fun createSendLocation(chatId: Long, latitude: Double, longitude: Double, replyMarkup: ReplyKeyboard?): SendLocation {
        return SendLocation.builder()
            .chatId(chatId.toString())
            .latitude(latitude)
            .longitude(longitude)
            .replyMarkup(replyMarkup)
            .build()
    }

    /**
     * Creates SendPhoto object with a given configuration.
     * @param chatId Chat id.
     * @param imageFileId Image file id in Telegram.
     * @param replyMarkup Reply keyboard.
     * @return SendPhoto object.
     */
    fun createSendPhoto(chatId: Long, imageFileId: String, replyMarkup: ReplyKeyboard? = null): SendPhoto {
        return SendPhoto.builder()
            .chatId(chatId.toString())
            .photo(InputFile(imageFileId))
            .replyMarkup(replyMarkup)
            .build()
    }

    /**
     * Creates EditMessageText object with a given configuration.
     * @param chatId Chat id.
     * @param messageId Message id to edit.
     * @param text Message text.
     * @param replyMarkup Reply keyboard.
     * @return EditMessageText object.
     */
    fun createEditMessageText(chatId: Long, messageId: Int, text: String, replyMarkup: InlineKeyboardMarkup? = null): EditMessageText {
        return EditMessageText.builder()
            .chatId(chatId.toString())
            .messageId(messageId)
            .text(text)
            .replyMarkup(replyMarkup)
            .build()
    }

    /**
     * Creates EditMessageMedia object with a given configuration.
     * @param chatId Chat id.
     * @param messageId Message id to edit.
     * @param imageFileId Image file id in Telegram.
     * @param replyMarkup Reply keyboard.
     * @return EditMessageMedia object.
     */
    fun createEditMessageMedia(chatId: Long, messageId: Int, imageFileId: String, replyMarkup: InlineKeyboardMarkup? = null): EditMessageMedia {
        return EditMessageMedia.builder()
            .chatId(chatId.toString())
            .messageId(messageId)
            .media(InputMediaPhoto(imageFileId))
            .replyMarkup(replyMarkup)
            .build()
    }

}