package com.telegram.xmasstree_bot.server.service.strategy

import com.telegram.xmasstree_bot.bot.XMassTreeBot
import com.telegram.xmasstree_bot.server.exception.InvalidArgumentException
import com.telegram.xmasstree_bot.server.entity.enums.UserState
import com.telegram.xmasstree_bot.server.service.RedisService
import com.telegram.xmasstree_bot.server.service.UserService
import com.telegram.xmasstree_bot.server.service.factory.BotPredefinedMessageFactory
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Message

/**
 * LocationMessageStrategy class. Implements MessageStrategy interface.
 * This class is responsible for processing location messages.
 * @see MessageStrategy
 */
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

    /**
     * Processes the location message.
     * @param message message from user.
     * @param bot bot instance.
     * @return BotApiMethod object.
     */
    private fun processLocation(message: Message, bot: XMassTreeBot): BotApiMethod<*>? {
        val user = userService.getOrCreateUser(message.from)
        /* If user is banned, then return null. */
        if (user.banned) return null

        /* If user is not in the LOCATION state, then return to the menu. */
        if (user.state != UserState.LOCATION) {
            userService.updateUserState(user, UserState.MENU)
            return botPredefinedMessageFactory.returnToMenuSend(message.chatId)
        }


        val location = "${message.location.latitude},${message.location.longitude}"
        try {
            /* Check that the location is within the borders of the city. */
            val userGeoBorderService = userService.getGeoBorderService(message.chatId)
            if (!userGeoBorderService.testPoint(message.location.latitude, message.location.longitude)) {
                userService.updateUserState(user, UserState.MENU)
                return botPredefinedMessageFactory.sendLocationOutOfBorderMessage(message.chatId)
            }
            /* Save location to Redis. */
            redisService.set(user.id.toString(), location)
            redisService.setExpiration(user.id.toString(), 60 * 60)
        } catch (e: Exception) {
            return when (e) {
                /* Location latitude or longitude is invalid. */
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
        /* Update user state to IMAGE. */
        userService.updateUserState(user, UserState.IMAGE)
        return botPredefinedMessageFactory.sendWaitForImage(message.chatId)
    }

}