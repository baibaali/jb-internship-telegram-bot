package com.telegram.xmasstree_bot.server.service.factory

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton

/**
 * KeyboardFactory class is used to create InlineKeyboardMarkup objects.
 */
@Component
class KeyboardFactory: AbstractFactory() {
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    /**
     * Creates InlineKeyboardMarkup object with a given configuration.
     * @param buttons List of buttons names.
     * @param rowConfiguration List of buttons count in each row.
     * @param callbacks List of buttons callbacks.
     */
    fun createInlineKeyboard(
        buttons: List<String>,
        rowConfiguration: List<Int>,
        callbacks: List<String>): InlineKeyboardMarkup? {

        /* Check if configuration is valid */
        if (buttons.size != callbacks.size || buttons.size != rowConfiguration.sum()) {
            logger.warn("createInlineKeyboard: Invalid parameters [buttonsName: $buttons, rowConfiguration: $rowConfiguration, buttonsCallbacks: $callbacks]")
            return null
        }

        val keyboard = getInlineKeyboard(buttons, rowConfiguration, callbacks)
        val inlineKeyboardMarkup = InlineKeyboardMarkup()
        inlineKeyboardMarkup.keyboard = keyboard
        return inlineKeyboardMarkup
    }

    /**
     * Creates InlineKeyboardMarkup object with a given configuration.
     * @param buttons List of buttons names.
     * @param rowConfiguration List of buttons count in each row.
     * @param callbacks List of buttons callbacks.
     */
    private fun getInlineKeyboard(
        buttons: List<String>,
        rowConfiguration: List<Int>,
        callbacks: List<String>): List<List<InlineKeyboardButton>> {


        val keyboard = mutableListOf<List<InlineKeyboardButton>>()
        var index = 0
        /* Create keyboard by rows */
        for (row in rowConfiguration) {
            keyboard.add(
                getInlineKeyboardButtonRow(buttons.subList(index, index + row), callbacks.subList(index, index + row))
            )
            index += row
        }
        return keyboard
    }

    /**
     * Creates keyboard row with a given configuration.
     * @param buttons List of buttons names.
     * @param callbacks List of buttons callbacks.
     */
    private fun getInlineKeyboardButtonRow(buttons: List<String>, callbacks: List<String>): List<InlineKeyboardButton> {
        val buttonsRow = mutableListOf<InlineKeyboardButton>()
        for (i in buttons.indices) {
            buttonsRow.add(getInlineKeyboardButton(buttons[i], callbacks[i]))
        }
        return buttonsRow
    }

    /**
     * Creates InlineKeyboardButton object with a given text and callback.
     * @param text Button name.
     * @param callback Button callback.
     */
    private fun getInlineKeyboardButton(text: String, callback: String): InlineKeyboardButton {
        val button = InlineKeyboardButton()
        button.text = text
        button.callbackData = callback
        return button
    }
}