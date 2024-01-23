package com.telegram.xmasstree_bot.server.repository

import com.telegram.xmasstree_bot.server.entity.User
import com.telegram.xmasstree_bot.server.entity.enums.City
import com.telegram.xmasstree_bot.server.entity.enums.UserState
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class UserRepositoryTest(@Autowired private val userRepository: UserRepository)
{
    @Test
    fun save() {
        val user = User(145102345, "username", City.PRAGUE, UserState.MENU, false)

        val savedUser = userRepository.save(user)

        assert(savedUser.id == user.id)
        assert(savedUser.username == user.username)
        assert(savedUser.city == user.city)
        assert(savedUser.state == user.state)
        assert(savedUser.banned == user.banned)
    }

    @Test
    fun findAll() {
        val user1 = User(145102345, "username1", City.PRAGUE, UserState.MENU, false)
        val user2 = User(345678901, "username2", City.PRAGUE, UserState.MENU, true)

        userRepository.save(user1)
        userRepository.save(user2)

        val foundUser = userRepository.findAll()

        assert(foundUser.size == 2)
    }

    @Test
    fun findById() {
        val user = User(145102345, "username", City.PRAGUE, UserState.MENU, false)

        userRepository.save(user)

        val foundUser = userRepository.findById(user.id)

        assert(foundUser.isPresent)
        assert(foundUser.get().id == user.id)
        assert(foundUser.get().username == user.username)
        assert(foundUser.get().city == user.city)
        assert(foundUser.get().state == user.state)
        assert(foundUser.get().banned == user.banned)
    }

    @Test
    fun update() {
        val user = User(145102345, "username", City.PRAGUE, UserState.MENU, false)

        userRepository.save(user)

        val found = userRepository.findById(user.id)
        assert(found.isPresent)

        found.get().username = "newUsername"
        found.get().banned = true

        val saved = userRepository.save(found.get())
        assert(saved.username == "newUsername")
        assert(saved.banned)
    }

    @Test
    fun delete() {
        val user = User(145102345, "username", City.PRAGUE, UserState.MENU, false)

        userRepository.save(user)

        val found = userRepository.findById(user.id)
        assert(found.isPresent)

        userRepository.delete(found.get())

        val deleted = userRepository.findById(user.id)
        assert(deleted.isEmpty)
    }
}
