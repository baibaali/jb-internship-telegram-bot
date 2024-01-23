package com.telegram.xmasstree_bot.server.service.factory

import com.telegram.xmasstree_bot.server.entity.XMassTree
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup

@Component
class BotPredefinedMessageFactory(
    private val messageFactory: MessageFactory,
    private val keyboardFactory: KeyboardFactory
) {
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

    fun sendTooManyUploadsMessage(chatId: Long, messageId: Int): BotApiMethod<*>? {
        return messageFactory.createEditMessageText(chatId, messageId, UPLOADS_LIMIT_EXCEEDED, createInlineKeyboardForReturnEdit())
    }

    fun sendSessionExpiredMessage(chatId: Long): BotApiMethod<*>? {
        return messageFactory.createSendMessage(chatId, SESSION_EXPIRED, createInlineKeyboardForReturnEdit())
    }
    fun returnToMenuEdit(chatId: Long, messageId: Int): BotApiMethod<*>? {
        return messageFactory.createEditMessageText(chatId, messageId, RETURN_TO_MENU, createInlineKeyboardForMenu())
    }

    fun returnToMenuSend(chatId: Long): BotApiMethod<*>? {
        return messageFactory.createSendMessage(chatId, RETURN_TO_MENU, createInlineKeyboardForMenu()
        )
    }

    fun displayPageMessageEdit(chatId: Long, messageId: Int, tree: XMassTree, totalPages: Int, page: Int):
            EditMessageMedia? {
        return messageFactory.createEditMessageMedia(
            chatId, messageId, tree.imageFileId, createInlineKeyboardForGalleryPage(page, totalPages, tree.id)
        )
    }

    fun displayPageMessageSend(chatId: Long, tree: XMassTree, totalPages: Int, page: Int): SendPhoto? {
        return messageFactory.createSendPhoto(
            chatId, tree.imageFileId, createInlineKeyboardForGalleryPage(page, totalPages, tree.id)
        )
    }

    fun sendEmptyGalleryMessage(chatId: Long, messageId: Int): BotApiMethod<*>? {
        return messageFactory.createEditMessageText(
            chatId, messageId, EMPTY_GALLERY, createInlineKeyboardForReturnEdit()
        )
    }

    fun sendWaitForLocation(chatId: Long, messageId: Int): BotApiMethod<*>? {
        return messageFactory.createEditMessageText(
            chatId, messageId, WAIT_FOR_LOCATION, createInlineKeyboardForReturnEdit()
        )
    }

    fun sendWaitForImage(chatId: Long): BotApiMethod<*>? {
        return messageFactory.createSendMessage(chatId, WAIT_FOR_IMAGE, createInlineKeyboardForReturnEdit())
    }

    fun sendStartMessage(chatId: Long): BotApiMethod<*> {
        return messageFactory.createSendMessage(chatId, START, createInlineKeyboardForMenu())
    }

    fun sendUnknownCommandMessage(chatId: Long): BotApiMethod<*> {
        return messageFactory.createSendMessage(chatId, UNKNOWN_COMMAND, createInlineKeyboardForReturnEdit())
    }

    fun sendOutdatedDataMessage(chatId: Long): BotApiMethod<*> {
        return messageFactory.createSendMessage(chatId, OUTDATED_DATA, createInlineKeyboardForReturnEdit())
    }

    fun sendLocationMessage(chatId: Long, latitude: Double, longitude: Double, page: Int, treeId:Long): BotApiMethod<*>? {
        return messageFactory.createSendLocation(
            chatId, latitude, longitude, createInlineKeyboardForLocationBack(page, treeId)
        )
    }

    fun sendLocationErrorMessage(chatId: Long): BotApiMethod<*> {
        return messageFactory.createSendMessage(chatId, LOCATION_ERROR, createInlineKeyboardForReturnEdit())
    }

    fun sendInternalErrorMessage(chatId: Long): BotApiMethod<*> {
        return messageFactory.createSendMessage(chatId, INTERNAL_ERROR, createInlineKeyboardForReturnEdit())
    }

    fun sendLocationOutOfBorderMessage(chatId: Long): BotApiMethod<*> {
        return messageFactory.createSendMessage(chatId, LOCATION_OUT_OF_BORDER, createInlineKeyboardForReturnEdit())
    }

    private fun createInlineKeyboardForGalleryPage(page: Int, totalPages: Int, treeId: Long): InlineKeyboardMarkup? {
        val previousPage = if (page - 1 < 0) totalPages - 1 else page - 1
        val nextPage = if (page + 1 >= totalPages) 0 else page + 1

        return keyboardFactory.createInlineKeyboard(
            listOf("Show Location", "<", "${page + 1}/$totalPages", ">", "Return"),
            listOf(1, 3, 1),
            listOf("showLocation,$page,$treeId", "previous,$previousPage", "dummy", "next,$nextPage", "returnSend")
        )
    }

    private fun createInlineKeyboardForReturnEdit(): InlineKeyboardMarkup? {
        return keyboardFactory.createInlineKeyboard(listOf("Return"), listOf(1), listOf("returnEdit"))
    }

    private fun createInlineKeyboardForReturnSend(): InlineKeyboardMarkup? {
        return keyboardFactory.createInlineKeyboard(listOf("Return"), listOf(1), listOf("returnSend"))
    }

    private fun createInlineKeyboardForMenu(): InlineKeyboardMarkup? {
        return keyboardFactory.createInlineKeyboard(listOf("New Tree", "Open Gallery"), listOf(1, 1), listOf("newTree", "displayGalleryPage"))
    }

    private fun createInlineKeyboardForLocationBack(page: Int, treeId: Long): InlineKeyboardMarkup? {
        return keyboardFactory.createInlineKeyboard(listOf("Back"), listOf(1), listOf("locationBack,$page,$treeId"))

    }

}