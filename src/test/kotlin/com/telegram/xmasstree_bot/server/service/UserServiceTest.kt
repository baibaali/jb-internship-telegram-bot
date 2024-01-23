package com.telegram.xmasstree_bot.server.service

import com.telegram.xmasstree_bot.server.entity.User
import com.telegram.xmasstree_bot.server.entity.enums.City
import com.telegram.xmasstree_bot.server.entity.enums.UserState
import com.telegram.xmasstree_bot.server.repository.UserRepository
import com.telegram.xmasstree_bot.server.service.geo.PragueGeoBorderService
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.test.web.servlet.MockMvc

@ExtendWith(MockitoExtension::class)
class UserServiceTest(
    @Mock private val userRepository: UserRepository,
    @Mock private val pragueGeoBorderService: PragueGeoBorderService
) {

    @InjectMocks
    private lateinit var userService: UserService

    @Test
    fun save() {
        val user = User(145102345, "username", City.PRAGUE, UserState.MENU, false)

        Mockito.`when`(userRepository.save(Mockito.any(User::class.java))).thenReturn(user)

        val savedUser = userService.save(user)

        assertEquals(user, savedUser)
    }

    @Test
    fun findAll() {
        val user1 = User(145102345, "username1", City.PRAGUE, UserState.MENU, false)
        val user2 = User(345678901, "username2", City.PRAGUE, UserState.MENU, true)

        Mockito.`when`(userRepository.findAll()).thenReturn(listOf(user1, user2))

        val foundUser = userService.findAll()

        assertEquals(2, foundUser.size)
    }

    @Test
    fun findAllPaging() {
        val user1 = User(145102345, "username1", City.PRAGUE, UserState.MENU, false)
        val user2 = User(345678901, "username2", City.PRAGUE, UserState.MENU, true)

        val pageRequest = PageRequest.of(0, 10)
        val page = PageImpl(listOf(user1, user2), pageRequest, 2)

        Mockito.`when`(userRepository.findAll(Mockito.any(PageRequest::class.java))).thenReturn(page)

        val foundUsers = userService.findAll(0, 10)

        assertEquals(2, foundUsers.totalElements)
    }

    @Test
    fun findById() {
        val user = User(145102345, "username", City.PRAGUE, UserState.MENU, false)

        Mockito.`when`(userRepository.findById(user.id)).thenReturn(java.util.Optional.of(user))

        val foundUser = userService.findById(user.id)

        assertTrue(foundUser.isPresent)
        assertEquals(user, foundUser.get())
    }



    @Test
    fun getGeoBorderServiceException() {
        val user = User(145102345, "username", City.PRAGUE, UserState.MENU, false)

        Mockito.`when`(userRepository.findById(user.id)).thenReturn(java.util.Optional.empty())

        assertThrows(RuntimeException::class.java) {
            userService.getGeoBorderService(user.id)
        }
    }

    @Test
    fun getGeoBorderService() {
        val user = User(145102345, "username", City.PRAGUE, UserState.MENU, false)

        Mockito.`when`(userRepository.findById(user.id)).thenReturn(java.util.Optional.of(user))

        val geoBorderService = userService.getGeoBorderService(user.id)

        assertEquals(pragueGeoBorderService, geoBorderService)
    }

    @Test
    fun getOrCreateUser_Create() {
        val user = User(145102345, "username", City.PRAGUE, UserState.MENU, false)

        Mockito.`when`(userRepository.findById(user.id)).thenReturn(java.util.Optional.empty())
        Mockito.`when`(userRepository.save(Mockito.any(User::class.java))).thenReturn(user)

        val tgUser = org.telegram.telegrambots.meta.api.objects.User()
        tgUser.id = user.id
        tgUser.userName = user.username
        val foundUser = userService.getOrCreateUser(tgUser)

        assertEquals(user, foundUser)
    }

    @Test
    fun getOrCreateUser_Get() {
        val user = User(145102345, "username", City.PRAGUE, UserState.MENU, false)

        Mockito.`when`(userRepository.findById(user.id)).thenReturn(java.util.Optional.of(user))
        Mockito.`when`(userRepository.save(Mockito.any(User::class.java))).thenReturn(user)

        val tgUser = org.telegram.telegrambots.meta.api.objects.User()
        tgUser.id = user.id
        tgUser.userName = user.username
        val foundUser = userService.getOrCreateUser(tgUser)

        assertEquals(user, foundUser)
    }

    @Test
    fun updateUserStateException() {
        val user = User(145102345, "username", City.PRAGUE, UserState.MENU, false)

        Mockito.`when`(userRepository.findById(user.id)).thenReturn(java.util.Optional.empty())

        assertThrows(RuntimeException::class.java) {
            userService.updateUserState(user, UserState.MENU)
        }
    }

    @Test
    fun updateUserState() {
        val userMenu = User(145102345, "username", City.PRAGUE, UserState.MENU, false)
        val userLocation = User(145102345, "username", City.PRAGUE, UserState.LOCATION, false)


        Mockito.`when`(userRepository.findById(userMenu.id)).thenReturn(java.util.Optional.of(userMenu))
        Mockito.`when`(userRepository.save(Mockito.any(User::class.java))).thenReturn(userLocation)

        val updatedUser = userService.updateUserState(userMenu, UserState.LOCATION)

        assertEquals(userLocation, updatedUser)
    }
}