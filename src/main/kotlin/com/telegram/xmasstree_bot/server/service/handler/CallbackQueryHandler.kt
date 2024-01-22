package com.telegram.xmasstree_bot.server.service.handler

import com.telegram.xmasstree_bot.bot.XMassTreeBot
import com.telegram.xmasstree_bot.server.entity.User
import com.telegram.xmasstree_bot.server.service.UserInteractionProcessor
import com.telegram.xmasstree_bot.server.service.UserService
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.CallbackQuery

@Service
class CallbackQueryHandler(
    private val userInteractionProcessor: UserInteractionProcessor,
    private val userService: UserService
): AbstractHandler() {
    override fun handle(botApiObject: BotApiObject, bot: XMassTreeBot): BotApiMethod<*>? {
        val callbackQuery = botApiObject as CallbackQuery
        val user = userService.findById(callbackQuery.message.chatId)
        if (user.isEmpty) {
            userService.save(User(callbackQuery.message.chatId, callbackQuery.message.from.userName))
            println("Saved")
        } else if(user.get().banned) {
            return null
        }
        return userInteractionProcessor.processCallbackQuery(callbackQuery, bot)
    }
}