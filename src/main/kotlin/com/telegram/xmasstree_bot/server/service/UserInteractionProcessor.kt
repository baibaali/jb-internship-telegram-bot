package com.telegram.xmasstree_bot.server.service

import com.telegram.xmasstree_bot.bot.XMassTreeBot
import com.telegram.xmasstree_bot.exception.GeoBorderException
import com.telegram.xmasstree_bot.exception.InvalidArgumentException
import com.telegram.xmasstree_bot.geo.GeoBorder
import com.telegram.xmasstree_bot.server.entity.XMassTree
import com.telegram.xmasstree_bot.server.service.common.AbstractInteractionProcessor
import com.telegram.xmasstree_bot.server.service.common.CallbackQueryProcessor
import com.telegram.xmasstree_bot.server.service.common.CommandProcessor
import com.telegram.xmasstree_bot.server.service.common.MessageProcessor
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
    private val xMassTreeService: XMassTreeService
): AbstractInteractionProcessor(), MessageProcessor, CallbackQueryProcessor, CommandProcessor {

    private var awaitingPhoto = false
    private var awaitingLocation = false
    private var trees = listOf<XMassTree>()
    private val userProgress = mutableMapOf<Long, Int>()
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
        val currentProgress = userProgress.getOrDefault(chatId, 0)

        val newProgress = when (callbackQuery.data) {
            "previous" -> if (currentProgress - 1 < 0) trees.size - 1 else currentProgress - 1
            "next" -> if (currentProgress + 1 == trees.size) 0 else currentProgress + 1
            else -> currentProgress
        }

        return when (callbackQuery.data) {
            "newTree" -> {
                awaitingLocation = true
                messageFactory.createEditMessageText(
                    chatId,
                    messageId,
                    "Please send me a location of your tree",
                    keyboardFactory.createInlineKeyboard(listOf("Return"), listOf(1), listOf("return_edit")))
            }
            "showTrees" -> {
                userProgress[chatId] = 0
                showTrees(chatId, messageId, bot)
            }
            "showLocation" -> {
                val tree = trees[userProgress[chatId]!!]
                messageFactory.createSendLocation(
                    chatId,
                    tree.location.split(",")[0].toDouble(),
                    tree.location.split(",")[1].toDouble(),
                    keyboardFactory.createInlineKeyboard(listOf("Return"), listOf(1), listOf("location_back"))
                )
            }
            "location_back" -> {
                userProgress[chatId] = newProgress
                showTrees(chatId, messageId, bot)
            }
            "return_edit" -> {
                awaitingLocation = false
                awaitingPhoto = false
                messageFactory.createEditMessageText(
                    chatId,
                    messageId,
                    "Please, select an option:",
                    keyboardFactory.createInlineKeyboard(
                        listOf("New Tree", "Show Trees"), listOf(1, 1), listOf("newTree", "showTrees")
                    )
                )
            }
            "return_send" -> {
                awaitingLocation = false
                awaitingPhoto = false
                sendStartMessage(chatId)
            }
            else -> {
                userProgress[chatId] = newProgress
                showTrees(chatId, messageId, bot, true)
            }
        }
    }

    private fun showTrees(chatId: Long, messageId: Int, bot: XMassTreeBot, messageExists: Boolean = false): BotApiMethod<*>? {
        // TODO: This should be paginated
        trees = xMassTreeService.findAll()

        if (trees.isEmpty()) {
            return messageFactory.createEditMessageText(
                chatId,
                messageId,
                "No trees found.",
                keyboardFactory.createInlineKeyboard(listOf("Return"), listOf(1), listOf("return_edit"))
            )
        } else {
            val tree = trees[userProgress[chatId]!!]

            if (!messageExists) {
                bot.execute(messageFactory.createSendPhoto(
                    chatId,
                    tree.imageFileId,
                    keyboardFactory.createInlineKeyboard(
                        listOf("Show Location", "<", ">", "Return"),
                        listOf(1, 2, 1),
                        listOf("showLocation", "previous", "next", "return_send"))
                ))
                return null
            } else {
                try {
                    bot.execute(messageFactory.createEditMessageMedia(
                        chatId,
                        messageId,
                        tree.imageFileId,
                        keyboardFactory.createInlineKeyboard(
                            listOf("Show Location", "<", ">", "Return"),
                            listOf(1, 2, 1),
                            listOf("showLocation", "previous", "next", "return_send"))
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
        }
    }

    override fun processCommand(message: Message, bot: XMassTreeBot): BotApiMethod<*>? {
        return when (message.text) {
            "/start" -> sendStartMessage(message.chatId)
            else -> sendUnknownCommandMessage(message.chatId)
        }
    }

    override fun processText(message: Message, bot: XMassTreeBot): BotApiMethod<*>? {
        awaitingLocation = false
        awaitingPhoto = false
        return messageFactory.createSendMessage(
            message.chatId,
            "Please, select an option:",
            keyboardFactory.createInlineKeyboard(
                listOf("New Tree", "Show Trees"), listOf(1, 1), listOf("newTree", "showTrees")
            )
        )
    }

    override fun processPhoto(message: Message, bot: XMassTreeBot): BotApiMethod<*>? {
        if (!awaitingPhoto) {
            awaitingLocation = false
            awaitingPhoto = false
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

        awaitingLocation = false
        awaitingPhoto = false

        return sendStartMessage(message.chatId)
    }

    override fun processLocation(message: Message, bot: XMassTreeBot): BotApiMethod<*>? {
        if (!awaitingLocation) {
            awaitingLocation = false
            awaitingPhoto = false
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
                return messageFactory.createSendMessage(
                    message.chatId,
                    "Sorry, the location must be within the Prague city.",
                    keyboardFactory.createInlineKeyboard(
                        listOf("Return"), listOf(1), listOf("return_edit")
                    )
                )
            }
        } catch (e: InvalidArgumentException) {
            return messageFactory.createSendMessage(
                message.chatId,
                "An error occurred while processing your location.\n" +
                        "Please try again and make sure that location is valid.",
                keyboardFactory.createInlineKeyboard(
                    listOf("Return"), listOf(1), listOf("return_edit")
                )
            )
        } catch (e: GeoBorderException) {
            e.printStackTrace()
            return messageFactory.createSendMessage(
                message.chatId,
                "An error occurred while processing your request.\n" +
                        "Please try again later.",
                keyboardFactory.createInlineKeyboard(
                    listOf("Return"), listOf(1), listOf("return_edit")
                )
            )
        }

        awaitingPhoto = true
        return messageFactory.createSendMessage(
            message.chatId,
            "Please, send me a photo of your tree",
            keyboardFactory.createInlineKeyboard(
                listOf("Return"), listOf(1), listOf("return_edit")
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
                listOf("Return"), listOf(1), listOf("return_edit"))
        )
    }


}