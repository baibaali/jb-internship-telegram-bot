package com.telegram.xmasstree_bot.server.service

import com.telegram.xmasstree_bot.bot.XMassTreeBot
import com.telegram.xmasstree_bot.exception.GeoBorderException
import com.telegram.xmasstree_bot.exception.InvalidArgumentException
import com.telegram.xmasstree_bot.server.service.geo.GeoBorder
import com.telegram.xmasstree_bot.server.entity.User
import com.telegram.xmasstree_bot.server.entity.XMassTree
import com.telegram.xmasstree_bot.server.entity.enums.UserState
import com.telegram.xmasstree_bot.server.service.factory.BotPredefinedMessageFactory
import com.telegram.xmasstree_bot.server.service.interaction.AbstractInteractionProcessor
import com.telegram.xmasstree_bot.server.service.interaction.CallbackQueryProcessor
import com.telegram.xmasstree_bot.server.service.interaction.CommandProcessor
import com.telegram.xmasstree_bot.server.service.interaction.MessageProcessor
import com.telegram.xmasstree_bot.server.service.factory.KeyboardFactory
import com.telegram.xmasstree_bot.server.service.factory.MessageFactory
import com.telegram.xmasstree_bot.server.service.strategy.MessageStrategy
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException

@Service
class UserInteractionProcessor: AbstractInteractionProcessor() {

    private lateinit var strategy: MessageStrategy

    fun setStrategy(strategy: MessageStrategy) {
        this.strategy = strategy
    }

    override fun processMessage(botApiObject: BotApiObject, bot: XMassTreeBot): BotApiMethod<*>? {
        return strategy.execute(botApiObject, bot)
    }

}