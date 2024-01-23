package com.telegram.xmasstree_bot.server.service.strategy

import com.telegram.xmasstree_bot.bot.XMassTreeBot
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject

@Service
class TextMessageStrategy: MessageStrategy {
    override fun execute(botApiObject: BotApiObject, bot: XMassTreeBot) {
        throw UnsupportedOperationException("Not yet implemented")
    }
}