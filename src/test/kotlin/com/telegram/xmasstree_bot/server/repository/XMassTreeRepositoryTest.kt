package com.telegram.xmasstree_bot.server.repository

import com.telegram.xmasstree_bot.server.entity.User
import com.telegram.xmasstree_bot.server.entity.XMassTree
import com.telegram.xmasstree_bot.server.entity.enums.City
import com.telegram.xmasstree_bot.server.entity.enums.UserState
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest


@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class XMassTreeRepositoryTest(@Autowired private val xMassTreeRepository: XMassTreeRepository)
{
    @Test
    fun save() {
        val tree = XMassTree( "10.0,12.0", "12345")

        val savedTree = xMassTreeRepository.save(tree)

        assert(savedTree.id == tree.id)
        assert(savedTree.location == tree.location)

    }

    @Test
    fun findAll() {
        val tree1 = XMassTree( "10.0,12.0", "12345")
        val tree2 = XMassTree( "15.0,12.0", "12346")

        xMassTreeRepository.save(tree1)
        xMassTreeRepository.save(tree2)

        val foundUser = xMassTreeRepository.findAll()

        assert(foundUser.size == 2)
    }

    @Test
    fun findById() {
        val tree1 = XMassTree( "10.0,12.0", "12345")

        xMassTreeRepository.save(tree1)

        val foundTree = xMassTreeRepository.findById(tree1.id)

        assert(foundTree.isPresent)
        assert(foundTree.get().id == tree1.id)
        assert(foundTree.get().location == tree1.location)
        assert(foundTree.get().imageFileId == tree1.imageFileId)
    }

    @Test
    fun update() {
        val tree1 = XMassTree( "10.0,12.0", "12345")

        xMassTreeRepository.save(tree1)

        val found = xMassTreeRepository.findById(tree1.id)
        assert(found.isPresent)

        found.get().location = "15.0,12.0"
        found.get().imageFileId = "00000"

        val saved = xMassTreeRepository.save(found.get())
        assert(saved.location == "15.0,12.0")
        assert(saved.imageFileId == "00000")
    }

    @Test
    fun delete() {
        val tree1 = XMassTree( "10.0,12.0", "12345")

        xMassTreeRepository.save(tree1)

        val found = xMassTreeRepository.findById(tree1.id)
        assert(found.isPresent)

        xMassTreeRepository.delete(found.get())

        val deleted = xMassTreeRepository.findById(tree1.id)
        assert(deleted.isEmpty)
    }
}
