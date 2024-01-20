package com.telegram.xmasstree_bot.bot

import com.telegram.xmasstree_bot.geo.GeoBorder
import com.telegram.xmasstree_bot.server.entity.XMassTree
import com.telegram.xmasstree_bot.server.service.XMassTreeService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendLocation
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import kotlin.math.*

/**
 * This is the main bot class.
 * It handles all the user interactions.
 */
@Component
class XMassTreeBot(@Value("\${telegram.botToken}") token: String, private val service: XMassTreeService): TelegramLongPollingBot(token) {

    private var awaitingLocation = false
    private var awaitingImage = false

    private var location = ""
    private var imageUrl = ""

    @Value("\${telegram.botName}")
    private val botName: String = ""

    private val cityBorder = GeoBorder()

    /* This is a map of user IDs to their progress in the bot.
     * It is used to track the user's progress when they are
     * browsing the trees.
     */
    private val userProgress = mutableMapOf<Long, Int>()

    private var trees = listOf<XMassTree>()

    override fun getBotUsername(): String = botName

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
                    showTrees(chatId)
                }
                else -> sendMainMenu(chatId, "Sorry, I don't understand you. Please choose an option:")
            }
        } /* If the user currently adding a new tree, then we expect a location */
        else if (update?.hasMessage() == true && update.message.hasLocation() && awaitingLocation) {
            location = "${update.message.location.latitude},${update.message.location.longitude}"
            sendMsg(update.message.chatId, "Location received: ${location.replace(".", "\\.")}")

            if (!cityBorder.testPoints(update.message.location.latitude, update.message.location.longitude)) {
                sendMsg(update.message.chatId, "Sorry, the location must be within the Prague city\\.")
                sendMainMenu(chatId = update.message.chatId, "Choose an option:")
                return
            }

            sendMsg(update.message.chatId, "Please send an image\\.")
            awaitingImage = true
        } /* After the user sent a location, we expect an image */
        else if (update?.hasMessage() == true && update.message.hasPhoto() && awaitingImage) {
            val photo = update.message.photo.last()  // Get the last photo (assuming it's the largest)
            imageUrl = photo.fileId

            // Save the tree entity to the database
            val tree = XMassTree(id = 0, location = location, imageUrl = imageUrl)
            service.save(tree)

            sendMsg(update.message.chatId, "Tree saved successfully\\!")

            // Reset the flags
            awaitingLocation = false
            awaitingImage = false

            sendMainMenu(chatId = update.message.chatId, "Choose an option:")
        } /* If the user clicked on a button, then we expect a callback query */
        else if (update?.hasCallbackQuery() == true) {
            handleCallbackQuery(update)
        }
    }

    /**
     * Send a message to the user with the given text followed by two option buttons.
     *
     * @param chatId The ID of the chat to send the message to
     * @param text The text of the message
     */
    private fun sendMainMenu(chatId: Long, text: String = "Welcome! Please choose an option:") {
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
        val row = ArrayList<InlineKeyboardButton>()

        val button1 = InlineKeyboardButton()
        button1.text = "New Tree"
        button1.callbackData = "newTree"
        row.add(button1)

        val button2 = InlineKeyboardButton()
        button2.text = "Show Trees"
        button2.callbackData = "showTrees"
        row.add(button2)

        val keyboard = ArrayList<List<InlineKeyboardButton>>()
        keyboard.add(row)

        inlineKeyboardMarkup.keyboard = keyboard
        return inlineKeyboardMarkup
    }

    /**
     * Show the next tree to the user.
     *
     * @param chatId The ID of the chat to send the message to.
     */
    private fun showTrees(chatId: Long) {
        // Retrieve trees from the database
        // TODO: This should be paginated
        trees = service.findAll()

        if (trees.isEmpty()) {
            val sendMessage = SendMessage(chatId.toString(), "No trees found.")
            sendMessage.replyMarkup = createInlineKeyboardMarkup()
            execute(sendMessage)
        } else {
            val tree = trees[userProgress[chatId]!!]

            val sendPhoto = SendPhoto()
            sendPhoto.chatId = chatId.toString()
            sendPhoto.photo = InputFile(tree.imageUrl)
            sendPhoto.replyMarkup = createInlineKeyboardMarkupForTree()

            execute(sendPhoto)
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
            "previous" -> maxOf(0, currentProgress - 1)
            "next" -> minOf(trees.size - 1, currentProgress + 1)
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
                showTrees(chatId)
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
                showTrees(chatId)
            }
            "back" -> sendMainMenu(chatId, "Choose an option:")
            else -> {
                userProgress[chatId] = newProgress
                showTrees(chatId)
            }
        }

    }

    /**
     * Sends the given text message to the user.
     *
     * @param chatId The ID of the chat to send the message to
     * @param text The text of the message to be sent
     */
    private fun sendMsg(chatId: Long, text: String) {
        val sendMessage = SendMessage(chatId.toString(), text)
        sendMessage.enableMarkdownV2(true)
        execute(sendMessage)
    }
}