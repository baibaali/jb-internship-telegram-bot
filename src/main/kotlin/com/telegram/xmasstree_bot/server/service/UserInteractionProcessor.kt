package com.telegram.xmasstree_bot.server.service

import com.telegram.xmasstree_bot.bot.XMassTreeBot
import com.telegram.xmasstree_bot.server.service.common.AbstractInteractionProcessor
import com.telegram.xmasstree_bot.server.service.common.CallbackQueryProcessor
import com.telegram.xmasstree_bot.server.service.common.CommandProcessor
import com.telegram.xmasstree_bot.server.service.common.MessageProcessor
import com.telegram.xmasstree_bot.server.service.factory.KeyboardFactory
import com.telegram.xmasstree_bot.server.service.factory.MessageFactory
import org.apache.logging.log4j.message.AbstractMessageFactory
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.Message

@Service
class UserInteractionProcessor(
    private val messageFactory: MessageFactory,
    private val keyboardFactory: KeyboardFactory
): AbstractInteractionProcessor(), MessageProcessor, CallbackQueryProcessor, CommandProcessor {
    override fun processCallbackQuery(callbackQuery: CallbackQuery, bot: XMassTreeBot): BotApiMethod<*> {
        throw NotImplementedError("Not yet implemented")
    }

    override fun processCommand(message: Message, bot: XMassTreeBot): BotApiMethod<*> {
        return when (message.text) {
            "/start" -> sendStartMessage(message.chatId, bot)
            else -> sendUnknownCommandMessage(message.chatId, bot)
        }
    }

    override fun processMessage(message: Message, bot: XMassTreeBot): BotApiMethod<*> {
        throw NotImplementedError("Not yet implemented")
    }

    private fun sendStartMessage(chatId: Long, bot: XMassTreeBot): BotApiMethod<*> {
        return messageFactory.createMessage(
            chatId,
            "Hello, I'm XMassTreeBot!",
            keyboardFactory.createInlineKeyboard(
                listOf("New Tree", "Show Trees"), listOf(1, 1), listOf("new_tree", "show_trees"))
        )
    }

    private fun sendUnknownCommandMessage(chatId: Long, bot: XMassTreeBot): BotApiMethod<*> {
        return messageFactory.createMessage(
            chatId,
            "Unknown command!",
            keyboardFactory.createInlineKeyboard(
                listOf("Return"), listOf(1), listOf("return"))
        )
    }


}