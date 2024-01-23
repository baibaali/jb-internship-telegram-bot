package com.telegram.xmasstree_bot.server.service.strategy

import com.telegram.xmasstree_bot.bot.XMassTreeBot
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject

interface MessageStrategy {
    fun execute(botApiObject: BotApiObject, bot: XMassTreeBot)
}