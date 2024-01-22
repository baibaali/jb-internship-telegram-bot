package com.telegram.xmasstree_bot.server.service

import com.telegram.xmasstree_bot.bot.XMassTreeBot
import com.telegram.xmasstree_bot.exception.GeoBorderException
import com.telegram.xmasstree_bot.exception.InvalidArgumentException
import com.telegram.xmasstree_bot.geo.GeoBorder
import com.telegram.xmasstree_bot.server.entity.User
import com.telegram.xmasstree_bot.server.entity.XMassTree
import com.telegram.xmasstree_bot.server.entity.enums.UserState
import com.telegram.xmasstree_bot.server.service.interaction.AbstractInteractionProcessor
import com.telegram.xmasstree_bot.server.service.interaction.CallbackQueryProcessor
import com.telegram.xmasstree_bot.server.service.interaction.CommandProcessor
import com.telegram.xmasstree_bot.server.service.interaction.MessageProcessor
import com.telegram.xmasstree_bot.server.service.factory.KeyboardFactory
import com.telegram.xmasstree_bot.server.service.factory.MessageFactory
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException

@Service
class UserInteractionProcessor(
    private val messageFactory: MessageFactory,
    private val keyboardFactory: KeyboardFactory,
    private val xMassTreeService: XMassTreeService,
    private val userService: UserService
): AbstractInteractionProcessor(), MessageProcessor, CallbackQueryProcessor, CommandProcessor {

    private var location = ""
    private var imageFileId = ""
    private var cityBorder: GeoBorder = GeoBorder()

    init {
        try {
            cityBorder.createPolygonFromGeoJson("/static/prague.geojson")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun processCallbackQuery(callbackQuery: CallbackQuery, bot: XMassTreeBot): BotApiMethod<*>? {
        val chatId = callbackQuery.message.chatId
        val messageId = callbackQuery.message.messageId

        val user = getUser(callbackQuery.from)

        println("callbackQuery.data = ${callbackQuery.data}")

        if (callbackQuery.data.startsWith("previous")) {
            val page = callbackQuery.data.split(",")[1].toIntOrNull() ?: 0
            return showTrees(chatId, bot, page, messageId)
        } else if (callbackQuery.data.startsWith("next")) {
            val page = callbackQuery.data.split(",")[1].toIntOrNull() ?: 0
            return showTrees(chatId, bot, page, messageId)
        } else if (callbackQuery.data.startsWith("showLocation")) {
            val page = callbackQuery.data.split(",")[1].toIntOrNull() ?: 0
            val treeId = callbackQuery.data.split(",")[2].toLongOrNull() ?: 0
            return showLocation(chatId, page, treeId)
        } else if (callbackQuery.data.startsWith("locationBack")) {
            val page = callbackQuery.data.split(",")[1].toIntOrNull() ?: 0
            val treeId = callbackQuery.data.split(",")[2].toLongOrNull() ?: 0
            return backToImage(chatId, bot, page, treeId)
        }

        return when (callbackQuery.data) {
            "newTree" -> {
                changeUserState(user, UserState.LOCATION)
                waitForLocation(chatId, messageId, bot)
            }
            "showTrees" -> {
                changeUserState(user, UserState.MENU)
                openImageGallery(chatId, bot, messageId)
            }
            "returnEdit" -> {
                changeUserState(user, UserState.MENU)
                returnToMenuEdit(chatId, messageId)
            }
            "returnSend" -> {
                changeUserState(user, UserState.MENU)
                returnToMenuSend(chatId)
            }
            else -> null
        }
    }

    private fun openImageGallery(chatId: Long, bot: XMassTreeBot, messageId: Int): BotApiMethod<*>? {
        try{
            val treeWrapper = xMassTreeService.findAll(0, 1)

            if (treeWrapper.isEmpty) {
                return messageFactory.createEditMessageText(
                    chatId,
                    messageId,
                    "No trees found.",
                    keyboardFactory.createInlineKeyboard(listOf("Return"), listOf(1), listOf("returnEdit"))
                )
            }

            return showTrees(chatId, bot, 0, null)
        } catch (e: IndexOutOfBoundsException) {
            return sendOutdatedDataMessage(chatId)
        }
    }

    private fun showTrees(chatId: Long, bot: XMassTreeBot, page: Int, messageId: Int? = null): BotApiMethod<*>? {
        try {
            val treeWrapper = xMassTreeService.findAll(page, 1)
            val totalPages = treeWrapper.totalPages

            val previousPage = if (page - 1 < 0) totalPages - 1 else page - 1
            val nextPage = if (page + 1 >= totalPages) 0 else page + 1

            val tree = treeWrapper.content[0]
            val currentPage = (page + 1).toString() + "/" + totalPages.toString()

            if (messageId == null) {
                bot.execute(messageFactory.createSendPhoto(
                    chatId,
                    tree.imageFileId,
                    keyboardFactory.createInlineKeyboard(
                        listOf("Show Location", "<", currentPage, ">", "Return"),
                        listOf(1, 3, 1),
                        listOf("showLocation,$page,${tree.id}", "previous,$previousPage", "dummy", "next,$nextPage", "returnSend"))
                ))
                return null
            } else {
                try {
                    bot.execute(messageFactory.createEditMessageMedia(
                        chatId,
                        messageId,
                        tree.imageFileId,
                        keyboardFactory.createInlineKeyboard(
                            listOf("Show Location", "<", currentPage, ">", "Return"),
                            listOf(1, 3, 1),
                            listOf("showLocation,$page,${tree.id}", "previous,$previousPage", "dummy", "next,$nextPage", "returnSend"))
                    ))
                    return null
                } catch (e: TelegramApiRequestException) {
                    if (e.apiResponse.contains("message is not modified")) {
                        return null
                    } else {
                        throw e
                    }
                }
            }
        } catch (e: IndexOutOfBoundsException) {
            return sendOutdatedDataMessage(chatId)
        }
    }

    private fun showLocation(chatId: Long, page: Int, treeId: Long): BotApiMethod<*>? {
        val tree = xMassTreeService.findById(treeId)
        if (tree.isEmpty) {
            return sendOutdatedDataMessage(chatId)
        }
        return messageFactory.createSendLocation(
            chatId,
            tree.get().location.split(",")[0].toDouble(),
            tree.get().location.split(",")[1].toDouble(),
            keyboardFactory.createInlineKeyboard(listOf("Back"), listOf(1), listOf("locationBack,$page,$treeId"))
        )
    }

    private fun backToImage(chatId: Long, bot: XMassTreeBot, page: Int, treeId: Long): BotApiMethod<*>? {
        try{
            val treeByPageNumber = xMassTreeService.findAll(page, 1)
            val treeById = xMassTreeService.findById(treeId)

            if (treeByPageNumber.isEmpty || treeById.isEmpty) {
                return sendOutdatedDataMessage(chatId)
            }

            if (treeByPageNumber.content[0].id != treeById.get().id) {
                return sendOutdatedDataMessage(chatId)
            }

            return showTrees(chatId, bot, page, null)
        } catch (e: IndexOutOfBoundsException) {
            return sendOutdatedDataMessage(chatId)
        }
    }

    private fun waitForLocation(chatId: Long, messageId: Int, bot: XMassTreeBot): BotApiMethod<*>? {
        return messageFactory.createEditMessageText(
            chatId,
            messageId,
            "Please send me a location of your tree",
            keyboardFactory.createInlineKeyboard(listOf("Return"), listOf(1), listOf("returnEdit")))
    }

    private fun returnToMenuEdit(chatId: Long, messageId: Int): BotApiMethod<*>? {
        return messageFactory.createEditMessageText(
            chatId,
            messageId,
            "Please, select an option:",
            keyboardFactory.createInlineKeyboard(
                listOf("New Tree", "Show Trees"), listOf(1, 1), listOf("newTree", "showTrees")
            )
        )
    }

    private fun returnToMenuSend(chatId: Long): BotApiMethod<*>? {
        return messageFactory.createSendMessage(
            chatId,
            "Please, select an option:",
            keyboardFactory.createInlineKeyboard(
                listOf("New Tree", "Show Trees"), listOf(1, 1), listOf("newTree", "showTrees")
            )
        )
    }

    override fun processCommand(message: Message, bot: XMassTreeBot): BotApiMethod<*>? {
        val user = getUser(message.from)
        changeUserState(user, UserState.MENU)

        return when (message.text) {
            "/start" -> sendStartMessage(message.chatId)
            else -> sendUnknownCommandMessage(message.chatId)
        }
    }

    override fun processText(message: Message, bot: XMassTreeBot): BotApiMethod<*>? {
        val user = getUser(message.from)
        changeUserState(user, UserState.MENU)

        return messageFactory.createSendMessage(
            message.chatId,
            "Please, select an option:",
            keyboardFactory.createInlineKeyboard(
                listOf("New Tree", "Show Trees"), listOf(1, 1), listOf("newTree", "showTrees")
            )
        )
    }

    override fun processPhoto(message: Message, bot: XMassTreeBot): BotApiMethod<*>? {
        val user = getUser(message.from)

        if (user.state != UserState.IMAGE) {
            changeUserState(user, UserState.MENU)

            return messageFactory.createSendMessage(
                message.chatId,
                "Please, select an option:",
                keyboardFactory.createInlineKeyboard(
                    listOf("New Tree", "Show Trees"), listOf(1, 1), listOf("newTree", "showTrees")
                )
            )
        }

        val photo = message.photo.last()
        imageFileId = photo.fileId

        val processingMessage = bot.execute(messageFactory.createSendMessage(
            message.chatId,
            "Processing...",
        ))

        val tree = XMassTree(location = location, imageFileId = imageFileId)
        xMassTreeService.save(tree)

        bot.execute(messageFactory.createEditMessageText(
            message.chatId,
            processingMessage.messageId,
            "Tree saved successfully!",
        ))

        changeUserState(user, UserState.MENU)

        return returnToMenuSend(message.chatId)
    }

    override fun processLocation(message: Message, bot: XMassTreeBot): BotApiMethod<*>? {
        val user = getUser(message.from)
        if (user.banned) return null

        if (user.state != UserState.LOCATION) {
            changeUserState(user, UserState.MENU)
            return messageFactory.createSendMessage(
                message.chatId,
                "Please, select an option:",
                keyboardFactory.createInlineKeyboard(
                    listOf("New Tree", "Show Trees"), listOf(1, 1), listOf("newTree", "showTrees")
                )
            )
        }

        location = "${message.location.latitude},${message.location.longitude}"
        try {
            if (!cityBorder.testPoints(message.location.latitude, message.location.longitude)) {
                changeUserState(user, UserState.MENU)
                return messageFactory.createSendMessage(
                    message.chatId,
                    "Sorry, the location must be within the Prague city.",
                    keyboardFactory.createInlineKeyboard(
                        listOf("Return"), listOf(1), listOf("returnEdit")
                    )
                )
            }
        } catch (e: InvalidArgumentException) {
            changeUserState(user, UserState.MENU)
            return messageFactory.createSendMessage(
                message.chatId,
                "An error occurred while processing your location.\n" +
                        "Please try again and make sure that location is valid.",
                keyboardFactory.createInlineKeyboard(
                    listOf("Return"), listOf(1), listOf("returnEdit")
                )
            )
        } catch (e: GeoBorderException) {
            e.printStackTrace()
            changeUserState(user, UserState.MENU)
            return messageFactory.createSendMessage(
                message.chatId,
                "An error occurred while processing your request.\n" +
                        "Please try again later.",
                keyboardFactory.createInlineKeyboard(
                    listOf("Return"), listOf(1), listOf("returnEdit")
                )
            )
        }

        changeUserState(user, UserState.IMAGE)
        return messageFactory.createSendMessage(
            message.chatId,
            "Please, send me a photo of your tree",
            keyboardFactory.createInlineKeyboard(
                listOf("Return"), listOf(1), listOf("returnEdit")
            )
        )
    }

    private fun sendStartMessage(chatId: Long): BotApiMethod<*> {
        return messageFactory.createSendMessage(
            chatId,
            "Hello, I'm XMassTreeBot!",
            keyboardFactory.createInlineKeyboard(
                listOf("New Tree", "Show Trees"), listOf(1, 1), listOf("newTree", "showTrees"))
        )
    }

    private fun sendUnknownCommandMessage(chatId: Long): BotApiMethod<*> {
        return messageFactory.createSendMessage(
            chatId,
            "Unknown command!",
            keyboardFactory.createInlineKeyboard(
                listOf("Return"), listOf(1), listOf("returnEdit"))
        )
    }

    private fun sendOutdatedDataMessage(chatId: Long): BotApiMethod<*> {
        return messageFactory.createSendMessage(
            chatId,
            "It seems that the data is outdated.\n" +
                    "Please, reload the gallery.",
            keyboardFactory.createInlineKeyboard(
                listOf("Return"), listOf(1), listOf("returnEdit"))
        )
    }

    private fun getUser(tgUser: org.telegram.telegrambots.meta.api.objects.User): User {
        val userWrapper = userService.findById(tgUser.id)
        if (userWrapper.isEmpty) {
            return User(
                id = tgUser.id,
                username = tgUser.userName,
                state = UserState.MENU,
                banned = false
            )
        }

        val user = userWrapper.get()
        user.username = tgUser.userName
        return userService.save(user)
    }

    private fun changeUserState(user: User, newState: UserState): User {
        user.state = newState
        return userService.save(user)
    }


}