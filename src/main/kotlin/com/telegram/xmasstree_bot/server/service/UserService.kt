package com.telegram.xmasstree_bot.server.service

import com.telegram.xmasstree_bot.server.service.geo.AbstractGeoBorderService
import com.telegram.xmasstree_bot.server.service.geo.PragueGeoBorderService
import com.telegram.xmasstree_bot.server.entity.User
import com.telegram.xmasstree_bot.server.entity.enums.City
import com.telegram.xmasstree_bot.server.entity.enums.UserState
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

        return when (user.get().city) {
            City.PRAGUE -> pragueGeoBorderService
            else -> pragueGeoBorderService
        }
    }

    fun getOrCreateUser(tgUser: org.telegram.telegrambots.meta.api.objects.User): User {
        val userWrapper = findById(tgUser.id)
        if (userWrapper.isEmpty) {
            return save(User(
                id = tgUser.id,
                username = tgUser.userName,
                state = UserState.MENU,
                banned = false
            ))
        }

        val user = userWrapper.get()
        user.username = tgUser.userName
        return save(user)
    }

    fun updateUserState(user: User, newState: UserState): User {
        val dbUser = findById(user.id)
        if (dbUser.isEmpty) {
            throw RuntimeException("User with id ${user.id} not found")
        }
        dbUser.get().state = newState
        return save(dbUser.get())
    }

}