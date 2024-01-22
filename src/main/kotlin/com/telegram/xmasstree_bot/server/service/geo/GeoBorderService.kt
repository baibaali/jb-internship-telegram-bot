package com.telegram.xmasstree_bot.server.service.geo

interface GeoBorderService {
    fun testPoint(latitude: Double, longitude: Double): Boolean
}