package com.telegram.xmasstree_bot.server.service.strategy

import com.telegram.xmasstree_bot.bot.XMassTreeBot
import com.telegram.xmasstree_bot.server.entity.enums.UserState
import com.telegram.xmasstree_bot.server.service.UserService
import com.telegram.xmasstree_bot.server.service.factory.BotPredefinedMessageFactory
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Message

/**
 * CommandMessageStrategy class. Implements MessageStrategy interface.
 * This class is responsible for processing command messages.
 * @see MessageStrategy
 */
@Service
class CommandMessageStrategy(
    private val userService: UserService,
    private val botPredefinedMessageFactory: BotPredefinedMessageFactory
): MessageStrategy {
    override fun execute(botApiObject: BotApiObject, bot: XMassTreeBot): BotApiMethod<*>? {
        val message = botApiObject as Message
        return processCommand(message, bot)
    }

    /**
     * Sends a message to the user depending on the command.
     * @param message message from user.
     * @param bot bot instance.
     * @return BotApiMethod object.
     */
    private fun processCommand(message: Message, bot: XMassTreeBot): BotApiMethod<*>? {
        val user = userService.getOrCreateUser(message.from)
        userService.updateUserState(user, UserState.MENU)

        return when (message.text) {
            "/start" -> botPredefinedMessageFactory.sendStartMessage(message.chatId)
            else -> botPredefinedMessageFactory.sendUnknownCommandMessage(message.chatId)
        }
    }
}