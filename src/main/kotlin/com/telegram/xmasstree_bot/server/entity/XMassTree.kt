package com.telegram.xmasstree_bot.server.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

/**
 * XMassTree entity class.
 */
@Entity
@Table(name = "trees")
data class XMassTree(
    /**
     * Location of the tree in the next format: "latitude,longitude".
     */
    @Column(name = "location", nullable = false)
    var location: String = "",
    /**
     * The telegram fileId property of the image.
     */
    @Column(name = "image_file_id", nullable = false)
    var imageFileId: String = ""
): AbstractEntity()

