package com.telegram.xmasstree_bot.server.service.geo

/**
 * GeoBorderService interface.
 */
interface GeoBorderService {
    fun testPoint(latitude: Double, longitude: Double): Boolean
}