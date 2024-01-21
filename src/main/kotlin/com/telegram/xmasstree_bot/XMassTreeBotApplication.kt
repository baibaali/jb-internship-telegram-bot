package com.telegram.xmasstree_bot

import com.telegram.xmasstree_bot.config.TelegramBotProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(TelegramBotProperties::class)
class XMassTreeBotApplication

fun main(args: Array<String>) {
    runApplication<XMassTreeBotApplication>(*args)
}
