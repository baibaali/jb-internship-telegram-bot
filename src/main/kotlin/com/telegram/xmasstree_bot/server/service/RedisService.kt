package com.telegram.xmasstree_bot.server.service

import jakarta.annotation.PreDestroy
import org.springframework.stereotype.Service
import redis.clients.jedis.Jedis

@Service
class RedisService {

    private val jedis: Jedis = Jedis()

    fun zadd(key: String, score: Double, value: String) {
        jedis.zadd(key, score, value)
    }

    fun zcount(key: String, min: Double, max: Double): Long {
        return jedis.zcount(key, min, max)
    }

    fun set(key: String, value: String) {
        jedis.set(key, value)
    }

    fun setExpiration(key: String, seconds: Long) {
        jedis.expire(key, seconds)
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