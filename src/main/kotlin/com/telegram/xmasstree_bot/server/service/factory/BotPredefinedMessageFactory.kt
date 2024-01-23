package com.telegram.xmasstree_bot.server.service.factory

import com.telegram.xmasstree_bot.server.entity.XMassTree
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup

@Service
class BotPredefinedMessageFactory(
    private val messageFactory: MessageFactory,
    private val keyboardFactory: KeyboardFactory
) {

    fun sendSessionExpiredMessage(chatId: Long): BotApiMethod<*>? {
        return messageFactory.createSendMessage(
            chatId,
            "It seems like you have been inactive for too long. Please, try again.",
            keyboardFactory.createInlineKeyboard(
                listOf("Return"), listOf(1), listOf("returnEdit")
            )
        )
    }
    fun returnToMenuEdit(chatId: Long, messageId: Int): BotApiMethod<*>? {
        return messageFactory.createEditMessageText(
            chatId,
            messageId,
            "Please, select an option:",
            keyboardFactory.createInlineKeyboard(
                listOf("New Tree", "Open Gallery"), listOf(1, 1), listOf("newTree", "displayGalleryPage")
            )
        )
    }

    fun returnToMenuSend(chatId: Long): BotApiMethod<*>? {
        return messageFactory.createSendMessage(
            chatId,
            "Please, select an option:",
            keyboardFactory.createInlineKeyboard(
                listOf("New Tree", "Open Gallery"), listOf(1, 1), listOf("newTree", "displayGalleryPage")
            )
        )
    }

    fun displayPageMessageEdit(chatId: Long, messageId: Int, tree: XMassTree, totalPages: Int, page: Int):
            EditMessageMedia? {
        return messageFactory.createEditMessageMedia(
            chatId,
            messageId,
            tree.imageFileId,
            createInlineKeyboardForGalleryPage(page, totalPages, tree.id)
        )
    }

    fun displayPageMessageSend(chatId: Long, tree: XMassTree, totalPages: Int, page: Int): SendPhoto? {
        return messageFactory.createSendPhoto(
            chatId,
            tree.imageFileId,
            createInlineKeyboardForGalleryPage(page, totalPages, tree.id)
        )
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

    fun sendEmptyGalleryMessage(chatId: Long, messageId: Int): BotApiMethod<*>? {
        return messageFactory.createEditMessageText(
            chatId,
            messageId,
            "There are no trees yet",
            keyboardFactory.createInlineKeyboard(listOf("Return"), listOf(1), listOf("returnEdit"))
        )
    }

    fun waitForLocation(chatId: Long, messageId: Int): BotApiMethod<*>? {
        return messageFactory.createEditMessageText(
            chatId,
            messageId,
            "Please send me a location of your tree",
            keyboardFactory.createInlineKeyboard(listOf("Return"), listOf(1), listOf("returnEdit")))
    }

    fun waitForImage(chatId: Long): BotApiMethod<*>? {
        return messageFactory.createSendMessage(
            chatId,
            "Please, send me a photo of your tree",
            keyboardFactory.createInlineKeyboard(
                listOf("Return"), listOf(1), listOf("returnEdit")
            )
        )
    }

    fun sendStartMessage(chatId: Long): BotApiMethod<*> {
        return messageFactory.createSendMessage(
            chatId,
            "Hello, I'm XMassTreeBot!",
            keyboardFactory.createInlineKeyboard(
                listOf("New Tree", "Open Gallery"), listOf(1, 1), listOf("newTree", "displayGalleryPage"))
        )
    }

    fun sendUnknownCommandMessage(chatId: Long): BotApiMethod<*> {
        return messageFactory.createSendMessage(
            chatId,
            "Unknown command!",
            keyboardFactory.createInlineKeyboard(
                listOf("Return"), listOf(1), listOf("returnEdit"))
        )
    }

    fun sendOutdatedDataMessage(chatId: Long): BotApiMethod<*> {
        return messageFactory.createSendMessage(
            chatId,
            "It seems that the data is outdated.\n" +
                    "Please, reload the gallery.",
            keyboardFactory.createInlineKeyboard(
                listOf("Return"), listOf(1), listOf("returnEdit"))
        )
    }

    fun sendLocationMessage(chatId: Long, tree: XMassTree, page: Int): BotApiMethod<*>? {
        return messageFactory.createSendLocation(
            chatId,
            tree.location.split(",")[0].toDouble(),
            tree.location.split(",")[1].toDouble(),
            keyboardFactory.createInlineKeyboard(listOf("Back"), listOf(1), listOf("locationBack,$page,${tree.id}"))
        )
    }

    fun sendLocationErrorMessage(chatId: Long): BotApiMethod<*> {
        return messageFactory.createSendMessage(
            chatId,
            "An error occurred while processing your location.\n" +
                    "Please try again and make sure that location is valid.",
            keyboardFactory.createInlineKeyboard(
                listOf("Return"), listOf(1), listOf("returnEdit")
            )
        )
    }

    fun sendInternalErrorMessage(chatId: Long): BotApiMethod<*> {
        return messageFactory.createSendMessage(
            chatId,
            "An error occurred while processing your request.\n" +
                    "Please try again later.",
            keyboardFactory.createInlineKeyboard(
                listOf("Return"), listOf(1), listOf("returnEdit")
            )
        )
    }

    fun sendLocationOutOfBorderMessage(chatId: Long): BotApiMethod<*> {
        return messageFactory.createSendMessage(
            chatId,
            "Sorry, the location must be within the Prague city.",
            keyboardFactory.createInlineKeyboard(
                listOf("Return"), listOf(1), listOf("returnEdit")
            )
        )
    }

}