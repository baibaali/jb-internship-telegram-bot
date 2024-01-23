package com.telegram.xmasstree_bot.server.service.factory

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton

@ExtendWith(MockitoExtension::class)
class KeyboardFactoryTest{

    @InjectMocks
    private lateinit var keyboardFactory: KeyboardFactory

    @Test
    fun createInlineKeyboard() {
        val buttons = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9")
        val rowConfiguration = listOf(3, 3, 3)
        val callbacks = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9")

        val keyboard = InlineKeyboardMarkup()
        val row1 = listOf(
            (InlineKeyboardButton.builder().text("1").callbackData("1").build()),
            (InlineKeyboardButton.builder().text("2").callbackData("2").build()),
            (InlineKeyboardButton.builder().text("3").callbackData("3").build())
        )
        val row2 = listOf(
            (InlineKeyboardButton.builder().text("4").callbackData("4").build()),
            (InlineKeyboardButton.builder().text("5").callbackData("5").build()),
            (InlineKeyboardButton.builder().text("6").callbackData("6").build())
        )
        val row3 = listOf(
            (InlineKeyboardButton.builder().text("7").callbackData("7").build()),
            (InlineKeyboardButton.builder().text("8").callbackData("8").build()),
            (InlineKeyboardButton.builder().text("9").callbackData("9").build())
        )

        keyboard.keyboard = listOf(row1, row2, row3)

        val result = keyboardFactory.createInlineKeyboard(buttons, rowConfiguration, callbacks)

        assertNotNull(result)
        assertEquals(keyboard, result)
    }

}