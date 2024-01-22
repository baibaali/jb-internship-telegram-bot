package com.telegram.xmasstree_bot.server.service.handler

import com.telegram.xmasstree_bot.bot.XMassTreeBot
import com.telegram.xmasstree_bot.server.entity.User
import com.telegram.xmasstree_bot.server.service.UserInteractionProcessor
import com.telegram.xmasstree_bot.server.service.UserService
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Message

@Service
class MessageHandler(
    private val userInteractionProcessor: UserInteractionProcessor,
    private val userService: UserService
): AbstractHandler() {
    override fun handle(botApiObject: BotApiObject, bot: XMassTreeBot): BotApiMethod<*>? {
        val message = botApiObject as Message
        val user = userService.findById(message.chatId)
        if (user.isEmpty) {
            userService.save(User(message.chatId, message.from.userName))
            println("Saved")
        } else if (user.get().banned) {
            return null
        }
        if (message.hasLocation()) {
            return userInteractionProcessor.processLocation(message, bot)
        } else if (message.hasPhoto()) {
            return userInteractionProcessor.processPhoto(message, bot)
        } else if (message.hasText()) {
            return userInteractionProcessor.processText(message, bot)
        }
        return null
    }
}