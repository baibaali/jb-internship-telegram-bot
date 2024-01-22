package com.telegram.xmasstree_bot.server.service.factory

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton

@Component
class KeyboardFactory: AbstractFactory() {

    private val log = LoggerFactory.getLogger(KeyboardFactory::class.java)

    fun createInlineKeyboard(
        buttons: List<String>,
        rowConfiguration: List<Int>,
        callbacks: List<String>): InlineKeyboardMarkup? {

        if (buttons.size != callbacks.size || buttons.size != rowConfiguration.sum()) {
            log.warn("createInlineKeyboard: Invalid parameters [buttonsName: $buttons, rowConfiguration: $rowConfiguration, buttonsCallbacks: $callbacks]")
            return null
        }

        val keyboard = getInlineKeyboard(buttons, rowConfiguration, callbacks)
        val inlineKeyboardMarkup = InlineKeyboardMarkup()
        inlineKeyboardMarkup.keyboard = keyboard
        return inlineKeyboardMarkup
    }

    private fun getInlineKeyboard(
        buttons: List<String>,
        rowConfiguration: List<Int>,
        callbacks: List<String>): List<List<InlineKeyboardButton>> {

        val keyboard = mutableListOf<List<InlineKeyboardButton>>()
        var index = 0
        for (row in rowConfiguration) {
            val buttonsRow = mutableListOf<InlineKeyboardButton>()
            for (i in 0 until row) {
                val button = InlineKeyboardButton()
                button.text = buttons[index]
                button.callbackData = callbacks[index]
                buttonsRow.add(button)
                index++
            }
            keyboard.add(buttonsRow)
        }
        return keyboard
    }

}