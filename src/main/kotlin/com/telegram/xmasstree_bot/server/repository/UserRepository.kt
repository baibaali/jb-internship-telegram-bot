package com.telegram.xmasstree_bot.server.repository

import com.telegram.xmasstree_bot.server.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository for User entity
 */
@Repository
interface UserRepository: JpaRepository<User, Long> {
}