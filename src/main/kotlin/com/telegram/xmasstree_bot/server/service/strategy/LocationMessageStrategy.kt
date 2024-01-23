package com.telegram.xmasstree_bot.server.service.strategy

import com.telegram.xmasstree_bot.bot.XMassTreeBot
import com.telegram.xmasstree_bot.exception.InvalidArgumentException
import com.telegram.xmasstree_bot.server.entity.enums.UserState
import com.telegram.xmasstree_bot.server.service.RedisService
import com.telegram.xmasstree_bot.server.service.UserService
import com.telegram.xmasstree_bot.server.service.factory.BotPredefinedMessageFactory
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Message

@Service
class LocationMessageStrategy(
    private val userService: UserService,
    private val botPredefinedMessageFactory: BotPredefinedMessageFactory,
    private val redisService: RedisService
): MessageStrategy {
    override fun execute(botApiObject: BotApiObject, bot: XMassTreeBot): BotApiMethod<*>? {
        val message = botApiObject as Message
        return processLocation(message, bot)
    }

    fun processLocation(message: Message, bot: XMassTreeBot): BotApiMethod<*>? {
        val user = userService.getOrCreateUser(message.from)
        if (user.banned) return null

        if (user.state != UserState.LOCATION) {
            userService.updateUserState(user, UserState.MENU)
            return botPredefinedMessageFactory.returnToMenuSend(message.chatId)
        }

        val location = "${message.location.latitude},${message.location.longitude}"
        try {
            val userGeoBorderService = userService.getGeoBorderService(message.chatId)
            if (!userGeoBorderService.testPoint(message.location.latitude, message.location.longitude)) {
                userService.updateUserState(user, UserState.MENU)
                return botPredefinedMessageFactory.sendLocationOutOfBorderMessage(message.chatId)
            }
            redisService.set(user.id.toString(), location)
        } catch (e: Exception) {
            return when (e) {
                is InvalidArgumentException -> {
                    userService.updateUserState(user, UserState.MENU)
                    botPredefinedMessageFactory.sendLocationErrorMessage(message.chatId)
                }
                else -> {
                    e.printStackTrace()
                    userService.updateUserState(user, UserState.MENU)
                    botPredefinedMessageFactory.sendInternalErrorMessage(message.chatId)
                }
            }
        }

        userService.updateUserState(user, UserState.IMAGE)
        return botPredefinedMessageFactory.waitForImage(message.chatId)
    }

}