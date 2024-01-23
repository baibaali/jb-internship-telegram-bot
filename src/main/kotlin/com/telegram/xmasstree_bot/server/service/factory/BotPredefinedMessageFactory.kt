package com.telegram.xmasstree_bot.server.service.factory

import com.telegram.xmasstree_bot.server.entity.XMassTree
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup

/**
 * Factory for creating all messages bot can send.
 * @param messageFactory factory for creating messages
 * @param keyboardFactory factory for creating keyboards
 * @see MessageFactory
 * @see KeyboardFactory
 */
@Component
class BotPredefinedMessageFactory(
    private val messageFactory: MessageFactory,
    private val keyboardFactory: KeyboardFactory
) {
    /* Response messages */
    companion object{
        private const val SESSION_EXPIRED = "It seems like you have been inactive for too long. Please, try again."
        private const val UNKNOWN_COMMAND = "Unknown command!"
        private const val OUTDATED_DATA = "It seems that the data is outdated.\n" +
                "Please, reload the gallery."
        private const val LOCATION_ERROR = "An error occurred while processing your location.\n" +
                "Please try again and make sure that location is valid."
        private const val INTERNAL_ERROR = "An error occurred while processing your request.\n" +
                "Please try again later."
        private const val LOCATION_OUT_OF_BORDER = "Sorry, the location must be within the Prague city."
        private const val WAIT_FOR_LOCATION = "Please send me a location of your tree"
        private const val WAIT_FOR_IMAGE = "Please, send me a photo of your tree"
        private const val START = "Hello, I'm XMassTreeBot!"
        private const val EMPTY_GALLERY = "There are no trees yet"
        private const val RETURN_TO_MENU = "Please, select an option:"
        private const val UPLOADS_LIMIT_EXCEEDED = "You have exceeded the limit of uploads per hour.\n" +
                "Please, try again later."
    }

    /**
     * Creates an EditMessageText method for sending a message about exceeding the limit of uploads per hour.
     * The message contains a button for returning to the main menu.
     * @param chatId chat id
     * @param messageId message id to be edited
     * @return EditMessageText BotApiMethod to be executed
     */
    fun sendTooManyUploadsMessage(chatId: Long, messageId: Int): BotApiMethod<*>? {
        return messageFactory.createEditMessageText(chatId, messageId, UPLOADS_LIMIT_EXCEEDED, createInlineKeyboardForReturnEdit())
    }

    /**
     * Creates a SendMessage method for sending a message to inform the user that the session has expired.
     * The message contains a button for returning to the main menu.
     * @param chatId chat id
     * @return SendMessage BotApiMethod to be executed
     */
    fun sendSessionExpiredMessage(chatId: Long): BotApiMethod<*>? {
        return messageFactory.createSendMessage(chatId, SESSION_EXPIRED, createInlineKeyboardForReturnEdit())
    }

    /**
     * Creates an EditMessageText method for sending a message with button for returning to the main menu.
     * Unlike the returnToMenuSend method, this method edits the message instead of sending a new one.
     * @param chatId chat id
     * @param messageId message id to be edited
     * @return EditMessageText BotApiMethod to be executed
     * @see returnToMenuSend
     */
    fun returnToMenuEdit(chatId: Long, messageId: Int): BotApiMethod<*>? {
        return messageFactory.createEditMessageText(chatId, messageId, RETURN_TO_MENU, createInlineKeyboardForMenu())
    }

    /**
     * Creates a SendMessage method for sending a message with button for returning to the main menu.
     * Unlike the returnToMenuEdit method, this method sends a new message instead of editing the existing one.
     * @param chatId chat id
     * @return SendMessage BotApiMethod to be executed
     * @see returnToMenuEdit
     */
    fun returnToMenuSend(chatId: Long): BotApiMethod<*>? {
        return messageFactory.createSendMessage(chatId, RETURN_TO_MENU, createInlineKeyboardForMenu()
        )
    }

    /**
     * Creates an EditMessageMedia method for changing the page of the gallery.
     * Unlike the displayPageMessageSend method, this method edits the message instead of sending a new one.
     * @param chatId chat id
     * @param messageId message id to be edited
     * @param tree tree to be displayed
     * @param totalPages total number of pages
     * @param page current page
     * @return EditMessageMedia BotApiMethod to be executed
     * @see displayPageMessageSend
     */
    fun displayPageMessageEdit(chatId: Long, messageId: Int, tree: XMassTree, totalPages: Int, page: Int):
            EditMessageMedia? {
        return messageFactory.createEditMessageMedia(
            chatId, messageId, tree.imageFileId, createInlineKeyboardForGalleryPage(page, totalPages, tree.id)
        )
    }

    /**
     * Creates a SendPhoto method for sending a message that represents the page of the gallery.
     * Unlike the displayPageMessageEdit method, this method sends a new message instead of editing the existing one.
     * @param chatId chat id
     * @param tree tree to be displayed
     * @param totalPages total number of pages
     * @param page current page
     * @return SendPhoto BotApiMethod to be executed
     * @see displayPageMessageEdit
     */
    fun displayPageMessageSend(chatId: Long, tree: XMassTree, totalPages: Int, page: Int): SendPhoto? {
        return messageFactory.createSendPhoto(
            chatId, tree.imageFileId, createInlineKeyboardForGalleryPage(page, totalPages, tree.id)
        )
    }

    /**
     * Creates an EditMessageText method for sending a message that informs the user that the gallery is empty.
     * The message contains a button for returning to the main menu.
     * @param chatId chat id
     * @param messageId message id to be edited
     * @return EditMessageText BotApiMethod to be executed
     */
    fun sendEmptyGalleryMessage(chatId: Long, messageId: Int): BotApiMethod<*>? {
        return messageFactory.createEditMessageText(
            chatId, messageId, EMPTY_GALLERY, createInlineKeyboardForReturnEdit()
        )
    }

    /**
     * Creates an EditMessageText method for sending a message after user clicks on the "New Tree" button.
     * The message contains a button for returning to the main menu.
     * @param chatId chat id
     * @param messageId message id to be edited
     * @return EditMessageText BotApiMethod to be executed
     */
    fun sendWaitForLocation(chatId: Long, messageId: Int): BotApiMethod<*>? {
        return messageFactory.createEditMessageText(
            chatId, messageId, WAIT_FOR_LOCATION, createInlineKeyboardForReturnEdit()
        )
    }

    /**
     * Creates a SendMessage method for sending a message after user sends a location in response to the "New Tree" button.
     * @param chatId chat id
     * @return SendMessage BotApiMethod to be executed
     */
    fun sendWaitForImage(chatId: Long): BotApiMethod<*>? {
        return messageFactory.createSendMessage(chatId, WAIT_FOR_IMAGE, createInlineKeyboardForReturnEdit())
    }

    /**
     * Creates a SendMessage method for sending a message after user sends '/start' command.
     * @param chatId chat id
     * @return SendMessage BotApiMethod to be executed
     */
    fun sendStartMessage(chatId: Long): BotApiMethod<*> {
        return messageFactory.createSendMessage(chatId, START, createInlineKeyboardForMenu())
    }

    /**
     * Creates a SendMessage method for sending a message after user sends unsupported command.
     * The message contains a button for returning to the main menu.
     * @param chatId chat id
     * @return SendMessage BotApiMethod to be executed
     */
    fun sendUnknownCommandMessage(chatId: Long): BotApiMethod<*> {
        return messageFactory.createSendMessage(chatId, UNKNOWN_COMMAND, createInlineKeyboardForReturnEdit())
    }

    /**
     * Creates a SendMessage method for sending a message if the data in gallery is outdated.
     * The message contains a button for returning to the main menu.
     * @param chatId chat id
     * @return SendMessage BotApiMethod to be executed
     */
    fun sendOutdatedDataMessage(chatId: Long): BotApiMethod<*> {
        return messageFactory.createSendMessage(chatId, OUTDATED_DATA, createInlineKeyboardForReturnEdit())
    }

    /**
     * Creates a SendMessage method for sending a message if the user clicked on the "Show Location" button in the gallery.
     * The message contains a button for returning to the gallery.
     * @param chatId chat id
     * @param latitude latitude of the tree
     * @param longitude longitude of the tree
     * @param page current page
     * @param treeId id of the tree on the page. Used to validate data consistency.
     * @return SendMessage BotApiMethod to be executed
     */
    fun sendLocationMessage(chatId: Long, latitude: Double, longitude: Double, page: Int, treeId:Long): BotApiMethod<*>? {
        return messageFactory.createSendLocation(
            chatId, latitude, longitude, createInlineKeyboardForLocationBack(page, treeId)
        )
    }

    /**
     * Creates a SendMessage method for sending a message if the user sent invalid location.
     * The message contains a button for returning to the main menu.
     * @param chatId chat id
     * @return SendMessage BotApiMethod to be executed
     */
    fun sendLocationErrorMessage(chatId: Long): BotApiMethod<*> {
        return messageFactory.createSendMessage(chatId, LOCATION_ERROR, createInlineKeyboardForReturnEdit())
    }

    /**
     * Creates a SendMessage method for sending a message if internal error occurred.
     * @param chatId chat id
     * @return SendMessage BotApiMethod to be executed
     */
    fun sendInternalErrorMessage(chatId: Long): BotApiMethod<*> {
        return messageFactory.createSendMessage(chatId, INTERNAL_ERROR, createInlineKeyboardForReturnEdit())
    }

    /**
     * Creates a SendMessage method for sending a message if the user sent a location that is outside the allowed area.
     * The message contains a button for returning to the main menu.
     * @param chatId chat id
     * @return SendMessage BotApiMethod to be executed
     */
    fun sendLocationOutOfBorderMessage(chatId: Long): BotApiMethod<*> {
        return messageFactory.createSendMessage(chatId, LOCATION_OUT_OF_BORDER, createInlineKeyboardForReturnEdit())
    }

    /**
     * Creates an InlineKeyboardMarkup for the gallery page.
     * @param page current page
     * @param totalPages total number of pages
     * @param treeId id of the tree on the page
     * @return InlineKeyboardMarkup
     */
    private fun createInlineKeyboardForGalleryPage(page: Int, totalPages: Int, treeId: Long): InlineKeyboardMarkup? {
        val previousPage = if (page - 1 < 0) totalPages - 1 else page - 1
        val nextPage = if (page + 1 >= totalPages) 0 else page + 1

        return keyboardFactory.createInlineKeyboard(
            listOf("Show Location", "<", "${page + 1}/$totalPages", ">", "Return"),
            listOf(1, 3, 1),
            listOf("showLocation,$page,$treeId", "previous,$previousPage", "dummy", "next,$nextPage", "returnSend")
        )
    }

    /**
     * Creates an InlineKeyboardMarkup for the return button of EditMessageTexts.
     * @return InlineKeyboardMarkup
     */
    private fun createInlineKeyboardForReturnEdit(): InlineKeyboardMarkup? {
        return keyboardFactory.createInlineKeyboard(listOf("Return"), listOf(1), listOf("returnEdit"))
    }

    /**
     * Creates an InlineKeyboardMarkup for the return button of SendMessages.
     * @return InlineKeyboardMarkup
     */
    private fun createInlineKeyboardForReturnSend(): InlineKeyboardMarkup? {
        return keyboardFactory.createInlineKeyboard(listOf("Return"), listOf(1), listOf("returnSend"))
    }

    /**
     * Creates an InlineKeyboardMarkup for the main menu.
     * @return InlineKeyboardMarkup
     */
    private fun createInlineKeyboardForMenu(): InlineKeyboardMarkup? {
        return keyboardFactory.createInlineKeyboard(listOf("New Tree", "Open Gallery"), listOf(1, 1), listOf("newTree", "displayGalleryPage"))
    }

    /**
     * Creates an InlineKeyboardMarkup for the location page in the gallery.
     * @param page current page
     * @param treeId id of the tree on the page
     * @return InlineKeyboardMarkup
     */
    private fun createInlineKeyboardForLocationBack(page: Int, treeId: Long): InlineKeyboardMarkup? {
        return keyboardFactory.createInlineKeyboard(listOf("Back"), listOf(1), listOf("locationBack,$page,$treeId"))

    }

}