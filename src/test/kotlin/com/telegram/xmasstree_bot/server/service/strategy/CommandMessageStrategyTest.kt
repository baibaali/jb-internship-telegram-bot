package com.telegram.xmasstree_bot.server.service.strategy

import com.telegram.xmasstree_bot.bot.XMassTreeBot
import com.telegram.xmasstree_bot.server.entity.User
import com.telegram.xmasstree_bot.server.entity.enums.UserState
import com.telegram.xmasstree_bot.server.service.UserService
import com.telegram.xmasstree_bot.server.service.factory.BotPredefinedMessageFactory
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.Message

@ExtendWith(MockitoExtension::class)
class CommandMessageStrategyTest(
    @Mock private val userService: UserService,
    @Mock private val botPredefinedMessageFactory: BotPredefinedMessageFactory
) {

    @Mock
    private lateinit var bot: XMassTreeBot

    @InjectMocks
    private lateinit var commandMessageStrategy: CommandMessageStrategy

    @Test
    fun executeStart() {
        val tgUser = org.telegram.telegrambots.meta.api.objects.User()
        val user = User()

        val message = Message()
        message.from = tgUser
        message.chat = Chat(1L, "private")
        message.text = "/start"

        val response = SendMessage()

        Mockito.`when`(userService.getOrCreateUser(tgUser)).thenReturn(user)
        Mockito.`when`(userService.updateUserState(user, UserState.MENU)).thenReturn(user)
        Mockito.`when`(botPredefinedMessageFactory.sendStartMessage(message.chatId)).thenReturn(response)


        val result = commandMessageStrategy.execute(message, bot)

        assertEquals(response, result)
    }

    @Test
    fun executeUnknownCommand() {
        val tgUser = org.telegram.telegrambots.meta.api.objects.User()
        val user = User()

        val message = Message()
        message.from = tgUser
        message.chat = Chat(1L, "private")
        message.text = "/unknownCommand"

        val response = SendMessage()

        Mockito.`when`(userService.getOrCreateUser(tgUser)).thenReturn(user)
        Mockito.`when`(userService.updateUserState(user, UserState.MENU)).thenReturn(user)
        Mockito.`when`(botPredefinedMessageFactory.sendUnknownCommandMessage(message.chatId)).thenReturn(response)


        val result = commandMessageStrategy.execute(message, bot)

        assertEquals(response, result)
    }
}