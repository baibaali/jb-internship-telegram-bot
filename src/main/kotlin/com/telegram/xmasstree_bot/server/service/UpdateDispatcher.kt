package com.telegram.xmasstree_bot.server.service

import com.telegram.xmasstree_bot.bot.XMassTreeBot
import com.telegram.xmasstree_bot.server.handler.CallbackQueryHandler
import com.telegram.xmasstree_bot.server.handler.CommandHandler
import com.telegram.xmasstree_bot.server.handler.MessageHandler
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Update

@Service
class UpdateDispatcher(
    private val messageHandler: MessageHandler,
    private val commandHandler: CommandHandler,
    private val callbackQueryHandler: CallbackQueryHandler
) {
    fun dispatch(update: Update, bot: XMassTreeBot): BotApiMethod<*>? {
        if (update.hasMessage()) {
            if (update.message.hasText()) {
                if (update.message.text.startsWith("/"))
                    return commandHandler.handle(update.message, bot)
                return messageHandler.handle(update.message, bot)
            }
            else if (update.message.hasPhoto()) {
                return messageHandler.handle(update.message, bot)
            }
            else if (update.message.hasLocation()) {
                return messageHandler.handle(update.message, bot)
            }
        } else if (update.hasCallbackQuery()) {
            return callbackQueryHandler.handle(update.callbackQuery, bot)
        }
        return null
    }
}