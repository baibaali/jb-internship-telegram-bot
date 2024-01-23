package com.telegram.xmasstree_bot.server.service.strategy

import com.telegram.xmasstree_bot.bot.XMassTreeBot
import com.telegram.xmasstree_bot.server.entity.XMassTree
import com.telegram.xmasstree_bot.server.entity.enums.UserState
import com.telegram.xmasstree_bot.server.service.RedisService
import com.telegram.xmasstree_bot.server.service.UserService
import com.telegram.xmasstree_bot.server.service.XMassTreeService
import com.telegram.xmasstree_bot.server.service.factory.BotPredefinedMessageFactory
import com.telegram.xmasstree_bot.server.service.factory.KeyboardFactory
import com.telegram.xmasstree_bot.server.service.factory.MessageFactory
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Message

/**
 * PhotoMessageStrategy class. Implements MessageStrategy interface.
 * This class is responsible for processing photo messages.
 * @see MessageStrategy
 */
@Service
class PhotoMessageStrategy(
    private val xMassTreeService: XMassTreeService,
    private val redisService: RedisService,
    private val messageFactory: MessageFactory,
    private val botPredefinedMessageFactory: BotPredefinedMessageFactory,
    private val userService: UserService
): MessageStrategy {
    override fun execute(botApiObject: BotApiObject, bot: XMassTreeBot): BotApiMethod<*>? {
        val message = botApiObject as Message
        return processPhoto(message, bot)
    }

    /**
     * Processes the photo sent by the user.
     * @param message message from user.
     * @param bot bot instance.
     * @return BotApiMethod object.
     */
    private fun processPhoto(message: Message, bot: XMassTreeBot): BotApiMethod<*>? {
        val user = userService.getOrCreateUser(message.from)

        /* If the user is not in the IMAGE state, then he is not uploading a photo. */
        if (user.state != UserState.IMAGE) {
            userService.updateUserState(user, UserState.MENU)
            return botPredefinedMessageFactory.returnToMenuSend(message.chatId)
        }

        /* If the user in the IMAGE state, then he is uploading a photo. */
        /* The location must be stored in the Redis database. */
        /* If the location is not found, then the session has expired. User should start over. */
        val location = redisService.get(message.chatId.toString())
            ?: return botPredefinedMessageFactory.sendSessionExpiredMessage(message.chatId)

        val photo = message.photo.last()
        val imageFileId = photo.fileId

        /* Send a message to the user that the photo is being processed. */
        val processingMessage = bot.execute(messageFactory.createSendMessage(message.chatId, "Processing..."))

        /* Save the tree to the database. */
        val tree = XMassTree(location = location, imageFileId = imageFileId)
        xMassTreeService.save(tree)

        /* Add the tree to the user's list of uploads, to limit the number of uploads. */
        val currentTimeMillis = System.currentTimeMillis().toDouble()
        redisService.zadd("uploads:${user.id}", currentTimeMillis, currentTimeMillis.toString())

        /* Edit the porcessing message to inform the user that the tree has been saved. */
        bot.execute(messageFactory.createEditMessageText(message.chatId, processingMessage.messageId, "Tree saved successfully!"))

        /* Return the user to the menu. */
        userService.updateUserState(user, UserState.MENU)
        /* Delete the location from the Redis database. */
        redisService.delete(message.chatId.toString())

        return botPredefinedMessageFactory.returnToMenuSend(message.chatId)
    }

}