package com.telegram.xmasstree_bot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "telegram")
data class TelegramBotProperties(
    private val botName: String? = null,
    private val botToken: String? = null,
    private val webHookUrl: String? = null
)
