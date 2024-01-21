package com.telegram.xmasstree_bot.server.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import lombok.Builder

/**
 * XMassTree entity class.
 */
@Entity
@Table(name = "trees")
data class XMassTree(
    /**
     * Location of the tree in the next format: "latitude,longitude".
     */
    var location: String = "",
    /**
     * The telegram fileId property of the image.
     */
    var imageUrl: String = ""
): AbstractEntity()

