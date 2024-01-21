package com.telegram.xmasstree_bot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "telegram")
data class TelegramBotProperties @ConstructorBinding constructor(
    private val botToken: String,
    private val botName: String,
    private val webHookUrl: String,
) {
    fun getBotToken(): String {
        return botToken
    }

    fun getBotName(): String {
        return botName
    }

    fun getWebHookUrl(): String {
        return webHookUrl
    }
}