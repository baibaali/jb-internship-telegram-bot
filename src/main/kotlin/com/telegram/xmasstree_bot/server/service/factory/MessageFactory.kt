package com.telegram.xmasstree_bot.server.service.factory

import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.send.SendMessage.SendMessageBuilder
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard

class MessageFactory: AbstractFactory() {

    fun createMessage(chatId: Long, replyMarkup: ReplyKeyboard, text: String): SendMessage {
        return SendMessage.builder()
            .chatId(chatId.toString())
            .replyMarkup(replyMarkup)
            .text(text)
            .build()
    }

}