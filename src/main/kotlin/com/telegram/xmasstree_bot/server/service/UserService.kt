package com.telegram.xmasstree_bot.server.service

import com.telegram.xmasstree_bot.server.entity.User
import com.telegram.xmasstree_bot.server.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {

    fun save(user: User) = userRepository.save(user)

    fun findById(id: Long) = userRepository.findById(id)

}