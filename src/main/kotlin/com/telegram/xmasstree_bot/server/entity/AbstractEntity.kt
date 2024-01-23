package com.telegram.xmasstree_bot.server.entity

import jakarta.persistence.*

/**
 * Abstract entity class.
 * Used as parent for concrete entities.
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
abstract class AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L
}