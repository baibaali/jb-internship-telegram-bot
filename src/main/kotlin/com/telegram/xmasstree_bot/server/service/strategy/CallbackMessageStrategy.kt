package com.telegram.xmasstree_bot.server.service.strategy

import com.telegram.xmasstree_bot.bot.XMassTreeBot
import com.telegram.xmasstree_bot.server.entity.enums.UserState
import com.telegram.xmasstree_bot.server.service.RedisService
import com.telegram.xmasstree_bot.server.service.UserService
import com.telegram.xmasstree_bot.server.service.XMassTreeService
import com.telegram.xmasstree_bot.server.service.factory.BotPredefinedMessageFactory
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException

/**
 * CallbackMessageStrategy class. Implements MessageStrategy interface.
 * This class is responsible for processing callback queries.
 * @see MessageStrategy
 */
@Service
class CallbackMessageStrategy(
    private val botPredefinedMessageFactory: BotPredefinedMessageFactory,
    private val userService: UserService,
    private val xMassTreeService: XMassTreeService,
    private val redisService: RedisService
): MessageStrategy {
    override fun execute(botApiObject: BotApiObject, bot: XMassTreeBot): BotApiMethod<*>? {
        val callbackQuery = botApiObject as CallbackQuery
        return processCallbackQuery(callbackQuery, bot)
    }

    /**
     * Processes callback query.
     * @param callbackQuery CallbackQuery object.
     * @param bot XMassTreeBot object.
     * @return BotApiMethod object.
     */
    private fun processCallbackQuery(callbackQuery: CallbackQuery, bot: XMassTreeBot): BotApiMethod<*>? {
        val chatId = callbackQuery.message.chatId
        val messageId = callbackQuery.message.messageId

        val user = userService.getOrCreateUser(callbackQuery.from)
        /* If user is banned, we should ignore all his requests */
        if (user.banned)
            return null

        /* Process callbacks from gallery */
        if (callbackQuery.data.startsWith("previous")) {
            val page = callbackQuery.data.split(",")[1].toIntOrNull() ?: 0
            return displayGalleryPage(chatId, bot, page, messageId)
        } else if (callbackQuery.data.startsWith("next")) {
            val page = callbackQuery.data.split(",")[1].toIntOrNull() ?: 0
            return displayGalleryPage(chatId, bot, page, messageId)
        } else if (callbackQuery.data.startsWith("showLocation")) {
            val page = callbackQuery.data.split(",")[1].toIntOrNull() ?: 0
            val treeId = callbackQuery.data.split(",")[2].toLongOrNull() ?: 0
            return showLocation(chatId, page, treeId)
        } else if (callbackQuery.data.startsWith("locationBack")) {
            val page = callbackQuery.data.split(",")[1].toIntOrNull() ?: 0
            val treeId = callbackQuery.data.split(",")[2].toLongOrNull() ?: 0
            return backToImage(chatId, bot, page, treeId)
        }

        /* Process callbacks from menu */
        return when (callbackQuery.data) {
            "newTree" -> {
                /* Count the number of uploads in the last hour */
                val currentTimeMillis = System.currentTimeMillis().toDouble()
                val uploadsCount = redisService.zcount(
                    "uploads:${chatId}", currentTimeMillis - 60 * 60 * 1000, currentTimeMillis
                )
                println("uploads:$chatId = $uploadsCount")
                /* If user uploaded more than 5 trees in the last hour, send him a message that he should wait */
                if (uploadsCount >= 5) {
                    userService.updateUserState(user, UserState.MENU)
                    botPredefinedMessageFactory.sendTooManyUploadsMessage(chatId, messageId)
                } else {
                    userService.updateUserState(user, UserState.LOCATION)
                    botPredefinedMessageFactory.sendWaitForLocation(chatId, messageId)
                }
            }
            "displayGalleryPage" -> {
                userService.updateUserState(user, UserState.MENU)
                openImageGallery(chatId, bot, messageId)
            }
            "returnEdit" -> {
                userService.updateUserState(user, UserState.MENU)
                botPredefinedMessageFactory.returnToMenuEdit(chatId, messageId)
            }
            "returnSend" -> {
                userService.updateUserState(user, UserState.MENU)
                botPredefinedMessageFactory.returnToMenuSend(chatId)
            }
            else -> null
        }
    }

    /**
     * Sends the message that starts the gallery.
     * @param chatId Chat id.
     * @param bot XMassTreeBot object.
     * @param messageId Message id.
     * @return BotApiMethod object.
     */
    private fun openImageGallery(chatId: Long, bot: XMassTreeBot, messageId: Int): BotApiMethod<*>? {
        try{
            val treeWrapper = xMassTreeService.findAll(0, 1)

            /* If calling from main menu, then we should send a new message */
            if (treeWrapper.isEmpty) {
                return botPredefinedMessageFactory.sendEmptyGalleryMessage(chatId, messageId)
            }

            /* If calling from gallery, then we should edit the message */
            return displayGalleryPage(chatId, bot, 0, null)
        } catch (e: IndexOutOfBoundsException) {
            return botPredefinedMessageFactory.sendOutdatedDataMessage(chatId)
        }
    }

    /**
     * Updates the message with the tree to the next/previous page.
     * @param chatId Chat id.
     * @param bot XMassTreeBot object.
     * @param page Page number.
     * @param messageId Message id. If null, then we should send a new message.
     */
    private fun displayGalleryPage(chatId: Long, bot: XMassTreeBot, page: Int, messageId: Int? = null): BotApiMethod<*>? {
        try {
            val treeWrapper = xMassTreeService.findAll(page, 1)
            val totalPages = treeWrapper.totalPages

            val tree = treeWrapper.content[0]

            if (messageId == null) {
                bot.execute(botPredefinedMessageFactory.displayPageMessageSend(chatId, tree, totalPages, page))
            } else {
                try {
                    bot.execute(botPredefinedMessageFactory.displayPageMessageEdit(chatId, messageId, tree, totalPages, page))
                } catch (e: TelegramApiRequestException) {
                    /* If message contains this string, it means we tried to modify message with the same data */
                    /* We should ignore that, to allow user to click on the next/prev buttons, when there is only 1 page */
                    if (!e.apiResponse.contains("message is not modified"))
                        throw e
                }
            }

            return null
        } catch (e: IndexOutOfBoundsException) {
            return botPredefinedMessageFactory.sendOutdatedDataMessage(chatId)
        }
    }

    /**
     * Sends the message with the location of the tree.
     * @param chatId Chat id.
     * @param page Page number.
     * @param treeId Tree id.
     */
    private fun showLocation(chatId: Long, page: Int, treeId: Long): BotApiMethod<*>? {
        val tree = xMassTreeService.findById(treeId)
        if (tree.isEmpty) {
            return botPredefinedMessageFactory.sendOutdatedDataMessage(chatId)
        }
        val latitude = tree.get().location.split(",")[0].toDouble()
        val longitude = tree.get().location.split(",")[1].toDouble()
        return botPredefinedMessageFactory.sendLocationMessage(chatId, latitude, longitude, page, treeId)
    }

    /**
     * Sends the message with the image of the tree (Returns from location to the gallery).
     */
    private fun backToImage(chatId: Long, bot: XMassTreeBot, page: Int, treeId: Long): BotApiMethod<*>? {
        try{
            val treeByPageNumber = xMassTreeService.findAll(page, 1)
            val treeById = xMassTreeService.findById(treeId)

            if (treeByPageNumber.isEmpty || treeById.isEmpty) {
                return botPredefinedMessageFactory.sendOutdatedDataMessage(chatId)
            }

            if (treeByPageNumber.content[0].id != treeById.get().id) {
                return botPredefinedMessageFactory.sendOutdatedDataMessage(chatId)
            }

            return displayGalleryPage(chatId, bot, page, null)
        } catch (e: IndexOutOfBoundsException) {
            return botPredefinedMessageFactory.sendOutdatedDataMessage(chatId)
        }
    }


}