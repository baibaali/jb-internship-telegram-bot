package com.telegram.xmasstree_bot.server.service

import jakarta.annotation.PreDestroy
import org.springframework.stereotype.Service
import redis.clients.jedis.Jedis

@Service
class RedisService {

    private val jedis: Jedis = Jedis()

    fun set(key: String, value: String) {
        jedis.set(key, value)
    }

    fun get(key: String): String? {
        return jedis.get(key)
    }

    fun delete(key: String) {
        jedis.del(key)
    }

    @PreDestroy
    fun closeJedisConnection() {
        jedis.close()
    }
}