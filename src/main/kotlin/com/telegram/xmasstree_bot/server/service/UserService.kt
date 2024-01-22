package com.telegram.xmasstree_bot.server.service

import com.telegram.xmasstree_bot.server.service.geo.AbstractGeoBorderService
import com.telegram.xmasstree_bot.server.service.geo.PragueGeoBorderService
import com.telegram.xmasstree_bot.server.entity.User
import com.telegram.xmasstree_bot.server.entity.enums.City
import com.telegram.xmasstree_bot.server.repository.UserRepository
import com.telegram.xmasstree_bot.server.service.common.AbstractEntityService
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    userRepository: UserRepository,
    private val pragueGeoBorderService: PragueGeoBorderService
): AbstractEntityService<JpaRepository<User, Long>, User, Long>(userRepository) {

    fun getGeoBorderService(userId: Long): AbstractGeoBorderService {
        val user = findById(userId)
        if (user.isEmpty) {
            throw RuntimeException("User with id $userId not found")
        }

        println("User city: ${user.get().city}")
        return when (user.get().city) {
            City.PRAGUE -> pragueGeoBorderService
            else -> pragueGeoBorderService
        }
    }

}