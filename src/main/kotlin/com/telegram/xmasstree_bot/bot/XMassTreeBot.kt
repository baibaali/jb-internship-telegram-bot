package com.telegram.xmasstree_bot.bot

import com.telegram.xmasstree_bot.config.TelegramBotProperties
import com.telegram.xmasstree_bot.exception.GeoBorderException
import com.telegram.xmasstree_bot.exception.InvalidArgumentException
import com.telegram.xmasstree_bot.geo.GeoBorder
import com.telegram.xmasstree_bot.server.entity.XMassTree
import com.telegram.xmasstree_bot.server.service.XMassTreeService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendLocation
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException
import kotlin.math.*

/**
 * This is the main bot class.
 * It handles all the user interactions.
 */
@Component
class XMassTreeBot(
    private val telegramBotProperties: TelegramBotProperties,
    private val service: XMassTreeService
): TelegramLongPollingBot(telegramBotProperties.getBotToken()) {

    private var awaitingLocation = false
    private var awaitingImage = false

    private var location = ""
    private var imageUrl = ""

    private val cityBorder = GeoBorder()

    /* This is a map of user IDs to their progress in the bot.
     * It is used to track the user's progress when they are
     * browsing the trees.
     */
    private val userProgress = mutableMapOf<Long, Int>()

    private var trees = listOf<XMassTree>()

    init {
        try {
            cityBorder.createPolygonFromGeoJson("/static/prague.geojson")
        } catch (e: GeoBorderException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getBotUsername(): String = telegramBotProperties.getBotName()

    /**
     * This method is handling all the user interactions.
     * It is called every time the user sends a message to the bot.
     *
     * @param update The update object containing the user's message
     */
    override fun onUpdateReceived(update: Update?) {
        if (update?.hasMessage() == true && update.message.hasText()) {
            val message = update.message
            val chatId = message.chatId

            when (message.text) {
                "/start" -> sendMainMenu(chatId)
                "New Tree" -> {
                    sendMsg(chatId, "Please send me location of your tree")
                    awaitingLocation = true
                }
                "Show Trees" -> {
                    userProgress[chatId] = 0
                    showTrees(chatId, update.message.messageId)
                }
                else -> sendMainMenu(chatId, "Sorry, I don't understand you. Please select an option:")
            }
        } /* If the user currently adding a new tree, then we expect a location */
        else if (update?.hasMessage() == true && update.message.hasLocation() && awaitingLocation) {
            location = "${update.message.location.latitude},${update.message.location.longitude}"
            try {
                if (!cityBorder.testPoints(update.message.location.latitude, update.message.location.longitude)) {
                    sendMsg(update.message.chatId, "Sorry, the location must be within the Prague city\\.")
                    sendMainMenu(chatId = update.message.chatId, "Please, select an option:")
                    return
                }
            } catch (e: InvalidArgumentException) {
                sendInvalidLocationMessage(update.message.chatId)
                return
            } catch (e: GeoBorderException) {
                sendServerError(update.message.chatId)
                return
            }

            sendMsg(update.message.chatId, "Please send an image\\.")
            awaitingImage = true
        } /* After the user sent a location, we expect an image */
        else if (update?.hasMessage() == true && update.message.hasPhoto() && awaitingImage) {
            val photo = update.message.photo.last()  // Get the last photo (assuming it's the largest)
            imageUrl = photo.fileId

            val processingMessage = sendMsg(update.message.chatId, "Processing\\.\\.\\.")

            // Save the tree entity to the database
            val tree = XMassTree(id = 0, location = location, imageUrl = imageUrl)
            service.save(tree)

            val editMessage = EditMessageText()
            editMessage.chatId = update.message.chatId.toString()
            editMessage.messageId = processingMessage.messageId
            editMessage.text = "Tree saved successfully!"
            execute(editMessage)
//            sendMsg(update.message.chatId, "Tree saved successfully\\!")

            // Reset the flags
            awaitingLocation = false
            awaitingImage = false

            sendMainMenu(chatId = update.message.chatId, "Please, select an option:")
        } /* If the user clicked on a button, then we expect a callback query */
        else if (update?.hasCallbackQuery() == true) {
            handleCallbackQuery(update)
        }
    }

    private fun sendInvalidLocationMessage(chatId: Long) {
        val text = "An error occurred while processing your location.\n" +
                "Please try again and make sure that location is valid."
        val sendMessage = SendMessage(chatId.toString(), text)
        sendMessage.replyMarkup = createInlineKeyboardMarkupForReturn()

        execute(sendMessage)
    }

    private fun sendServerError(chatId: Long) {
        val text = "An error occurred while processing your request.\n" +
                "Please try again later."
        val sendMessage = SendMessage(chatId.toString(), text)
        sendMessage.replyMarkup = createInlineKeyboardMarkupForReturn()

        execute(sendMessage)
    }

    /**
     * Send a message to the user with the given text followed by two option buttons.
     *
     * @param chatId The ID of the chat to send the message to
     * @param text The text of the message
     */
    private fun sendMainMenu(chatId: Long, text: String = "Welcome! Please select an option:") {
        val sendMessage = SendMessage(chatId.toString(), text)
        sendMessage.replyMarkup = createInlineKeyboardMarkup()

        execute(sendMessage)
    }

    /**
     * Creates an inline keyboard with two buttons.
     *   "New Tree" - to add a new tree
     *   "Show Trees" - to browse the existing trees
     */
    private fun createInlineKeyboardMarkup(): InlineKeyboardMarkup {
        val inlineKeyboardMarkup = InlineKeyboardMarkup()
        val row1 = ArrayList<InlineKeyboardButton>()
        val row2 = ArrayList<InlineKeyboardButton>()

        val button1 = InlineKeyboardButton()
        button1.text = "New Tree"
        button1.callbackData = "newTree"
        row1.add(button1)

        val button2 = InlineKeyboardButton()
        button2.text = "Show Trees"
        button2.callbackData = "showTrees"
        row2.add(button2)

        val keyboard = ArrayList<List<InlineKeyboardButton>>()
        keyboard.add(row1)
        keyboard.add(row2)

        inlineKeyboardMarkup.keyboard = keyboard
        return inlineKeyboardMarkup
    }

    /**
     * Show the next tree to the user.
     *
     * @param chatId The ID of the chat to send the message to.
     */
    private fun showTrees(chatId: Long, editMessageId: Int, messageExists: Boolean = false) {
        // TODO: This should be paginated
        trees = service.findAll()

        if (trees.isEmpty()) {
            val editMessageText = EditMessageText()
            editMessageText.chatId = chatId.toString()
            editMessageText.messageId = editMessageId
            editMessageText.text = "No trees found."
            editMessageText.replyMarkup = createInlineKeyboardMarkupForReturn()
            execute(editMessageText)
        } else {
            val tree = trees[userProgress[chatId]!!]

            if (!messageExists) {
                val sendPhoto = SendPhoto()
                sendPhoto.chatId = chatId.toString()
                sendPhoto.photo = InputFile(tree.imageUrl)
                sendPhoto.replyMarkup = createInlineKeyboardMarkupForTree()
                execute(sendPhoto)
            } else {
                try {
                    val editMessageMedia = EditMessageMedia()
                    editMessageMedia.chatId = chatId.toString()
                    editMessageMedia.messageId = editMessageId
                    editMessageMedia.media = InputMediaPhoto(tree.imageUrl)
                    editMessageMedia.replyMarkup = createInlineKeyboardMarkupForTree()
                    execute(editMessageMedia)
                } catch (e: TelegramApiRequestException) {
                    if (e.apiResponse.contains("message is not modified")) {
                        // Do nothing
                    } else {
                        throw e
                    }
                }
            }

        }
    }

    /**
     * Creates an inline keyboard for the Tree item with four buttons.
     *   "Show Location" - to show the location of the tree
     *   "<" - to show the previous tree
     *   ">" - to show the next tree
     *   "Back" - to go back to the main menu
     */
    private fun createInlineKeyboardMarkupForTree(): InlineKeyboardMarkup {
        val inlineKeyboardMarkup = InlineKeyboardMarkup()
        val keyboard = ArrayList<List<InlineKeyboardButton>>()

        val rowUpper = ArrayList<InlineKeyboardButton>()

        val location = InlineKeyboardButton()
        location.text = "Show Location"
        location.callbackData = "location"
        rowUpper.add(location)

        keyboard.add(rowUpper)

        val rowMiddle = ArrayList<InlineKeyboardButton>()

        val previous = InlineKeyboardButton()
        previous.text = "<"
        previous.callbackData = "previous"
        rowMiddle.add(previous)

        val next = InlineKeyboardButton()
        next.text = ">"
        next.callbackData = "next"
        rowMiddle.add(next)

        keyboard.add(rowMiddle)

        val rowLower = ArrayList<InlineKeyboardButton>()

        val back = InlineKeyboardButton()
        back.text = "Back"
        back.callbackData = "back"
        rowLower.add(back)

        keyboard.add(rowLower)

        inlineKeyboardMarkup.keyboard = keyboard

        return inlineKeyboardMarkup
    }

    /**
     * Creates an inline keyboard with one button for Location item.
     *   "Back" - to go back to the tree
     */
    private fun createInlineKeyboardMarkupForLocation(): InlineKeyboardMarkup {
        val inlineKeyboardMarkup = InlineKeyboardMarkup()
        val keyboard = ArrayList<List<InlineKeyboardButton>>()

        val row = ArrayList<InlineKeyboardButton>()

        val location = InlineKeyboardButton()
        location.text = "Back"
        location.callbackData = "location_back"
        row.add(location)

        keyboard.add(row)

        inlineKeyboardMarkup.keyboard = keyboard
        return inlineKeyboardMarkup
    }

    /**
     * Creates an inline keyboard with one button for invalid Location item.
     *  "Return" - to go back to the main menu
     */
    private fun createInlineKeyboardMarkupForReturn(): InlineKeyboardMarkup {
        val inlineKeyboardMarkup = InlineKeyboardMarkup()
        val keyboard = ArrayList<List<InlineKeyboardButton>>()

        val row = ArrayList<InlineKeyboardButton>()

        val location = InlineKeyboardButton()
        location.text = "Return"
        location.callbackData = "return_to_main_menu"
        row.add(location)

        keyboard.add(row)

        inlineKeyboardMarkup.keyboard = keyboard
        return inlineKeyboardMarkup
    }

    /**
     * Handles callback queries.
     *
     * @param update the update object with the callback query
     */
    private fun handleCallbackQuery(update: Update) {
        val callbackQuery = update.callbackQuery
        val chatId = callbackQuery.message.chatId
        val currentProgress = userProgress.getOrDefault(chatId, 0)

        /* If user clicked on a button to show the next or previous tree, then we update the user progress */
        val newProgress = when (callbackQuery.data) {
            "previous" -> if (currentProgress - 1 < 0) trees.size - 1 else currentProgress - 1
            "next" -> if (currentProgress + 1 == trees.size) 0 else currentProgress + 1
            else -> currentProgress
        }

        /* Select the action based on the callback query data */
        when (callbackQuery.data) {
            "newTree" -> {
                sendMsg(chatId, "Please send me location of your tree")
                awaitingLocation = true
            }
            "showTrees" -> {
                userProgress[chatId] = 0
                showTrees(chatId, callbackQuery.message.messageId)
            }
            "location" -> {
                val tree = trees[userProgress[chatId]!!]
                val sendLocation = SendLocation()
                sendLocation.chatId = chatId.toString()
                sendLocation.latitude = tree.location.split(",")[0].toDouble()
                sendLocation.longitude = tree.location.split(",")[1].toDouble()
                sendLocation.replyMarkup = createInlineKeyboardMarkupForLocation()
                execute(sendLocation)
            }
            "location_back" -> {
                userProgress[chatId] = newProgress
                showTrees(chatId, callbackQuery.message.messageId)
            }
            "back" -> sendMainMenu(chatId, "Please, select an option:")
            "return_to_main_menu" -> {
                val editMessageText = EditMessageText()
                editMessageText.chatId = chatId.toString()
                editMessageText.messageId = callbackQuery.message.messageId
                editMessageText.text = "Please, select an option:"
                editMessageText.replyMarkup = createInlineKeyboardMarkup()
                execute(editMessageText)
            }
            else -> {
                userProgress[chatId] = newProgress
                showTrees(chatId, callbackQuery.message.messageId, true)
            }
        }

    }

    /**
     * Sends the given text message to the user.
     *
     * @param chatId The ID of the chat to send the message to
     * @param text The text of the message to be sent
     * @return The message that was sent
     */
    private fun sendMsg(chatId: Long, text: String): Message
    {
        val sendMessage = SendMessage(chatId.toString(), text)
        sendMessage.enableMarkdownV2(true)
        return execute(sendMessage)
    }
}