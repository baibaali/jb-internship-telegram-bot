package com.telegram.xmasstree_bot.server.entity

import com.telegram.xmasstree_bot.server.entity.enums.City
import com.telegram.xmasstree_bot.server.entity.enums.UserState
import jakarta.persistence.*

/**
 * User entity class.
 */
@Entity
@Table(name = "users")
data class User(
    @Id
    val id: Long = 0L,

    @Column(name = "username")
    var username: String = "",

    @Enumerated(EnumType.STRING)
    @Column(name = "city")
    var city: City = City.PRAGUE,

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    var state: UserState = UserState.MENU,

    @Column(name = "banned", nullable = false)
    var banned: Boolean = false
)