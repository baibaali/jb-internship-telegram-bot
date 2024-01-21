package com.telegram.xmasstree_bot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "telegram")
class TelegramBotProperties() {
    private val botName: String = ""
    private val botToken: String = ""
    private val webHookUrl: String = ""

    fun getBotName(): String {
        return botName
    }

    fun getBotToken(): String {
        return botToken
    }

    fun getWebHookUrl(): String {
        return webHookUrl
    }
}
