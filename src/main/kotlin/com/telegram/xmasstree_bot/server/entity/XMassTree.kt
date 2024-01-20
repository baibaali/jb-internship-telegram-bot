package com.telegram.xmasstree_bot.server.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

/**
 * XMassTree entity class.
 */
@Entity
data class XMassTree(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,
    /**
     * Location of the tree in the next format: "latitude,longitude".
     */
    val location: String,
    /**
     * The telegram fileId property of the image.
     */
    val imageUrl: String) {
    constructor() : this(0, "", "")
}

