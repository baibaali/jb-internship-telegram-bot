package com.telegram.xmasstree_bot.server.service.factory

import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard

@Component
class MessageFactory: AbstractFactory() {

    fun createMessage(chatId: Long, text: String, replyMarkup: ReplyKeyboard?): SendMessage {
        return SendMessage.builder()
            .chatId(chatId.toString())
            .replyMarkup(replyMarkup)
            .text(text)
            .build()
    }

}