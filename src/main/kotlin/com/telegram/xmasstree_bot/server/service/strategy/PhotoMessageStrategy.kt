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

    private fun processPhoto(message: Message, bot: XMassTreeBot): BotApiMethod<*>? {
        val user = userService.getOrCreateUser(message.from)

        if (user.state != UserState.IMAGE) {
            userService.updateUserState(user, UserState.MENU)
            return botPredefinedMessageFactory.returnToMenuSend(message.chatId)
        }

        val location = redisService.get(message.chatId.toString())
            ?: return botPredefinedMessageFactory.sendSessionExpiredMessage(message.chatId)

        val photo = message.photo.last()
        val imageFileId = photo.fileId

        val processingMessage = bot.execute(messageFactory.createSendMessage(message.chatId, "Processing..."))

        val tree = XMassTree(location = location, imageFileId = imageFileId)
        xMassTreeService.save(tree)

        val currentTimeMillis = System.currentTimeMillis().toDouble()
        redisService.zadd("uploads:${user.id}", currentTimeMillis, currentTimeMillis.toString())

        bot.execute(messageFactory.createEditMessageText(message.chatId, processingMessage.messageId, "Tree saved successfully!"))

        userService.updateUserState(user, UserState.MENU)

        redisService.delete(message.chatId.toString())

        return botPredefinedMessageFactory.returnToMenuSend(message.chatId)
    }

}