package com.telegram.xmasstree_bot.server.service

import com.telegram.xmasstree_bot.server.entity.XMassTree
import com.telegram.xmasstree_bot.server.repository.XMassTreeRepository
import com.telegram.xmasstree_bot.server.service.geo.PragueGeoBorderService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.util.*

@ExtendWith(MockitoExtension::class)
class XMassTreeServiceTest(
    @Mock private val xMassTreeRepository: XMassTreeRepository,
) {
    @InjectMocks
    private lateinit var xMassTreeService: XMassTreeService

    @Test
    fun save() {
        val xMassTree = XMassTree("10.0,20.0", "12345")

        Mockito.`when`(xMassTreeRepository.save(Mockito.any(XMassTree::class.java))).thenReturn(xMassTree)

        val savedTree = xMassTreeService.save(xMassTree)

        assertEquals(xMassTree, savedTree)
    }

    @Test
    fun findAll() {
        val xMassTree1 = XMassTree("10.0,20.0", "12345")
        val xMassTree2 = XMassTree("20.0,10.0", "54321")

        Mockito.`when`(xMassTreeRepository.findAll()).thenReturn(listOf(xMassTree1, xMassTree2))

        val foundTrees = xMassTreeService.findAll()

        assertEquals(2, foundTrees.size)
    }

    @Test
    fun findAllPaging() {
        val xMassTree1 = XMassTree("10.0,20.0", "12345")
        val xMassTree2 = XMassTree("20.0,10.0", "54321")

        val pageRequest = PageRequest.of(0, 10)
        val page = PageImpl(listOf(xMassTree1, xMassTree2), pageRequest, 2)

        Mockito.`when`(xMassTreeRepository.findAll(Mockito.any(PageRequest::class.java))).thenReturn(page)

        val foundTrees = xMassTreeService.findAll(0, 10)

        assertEquals(2, foundTrees.totalElements)
    }

    @Test
    fun findById() {
        val xMassTree = XMassTree("10.0,20.0", "12345")

        Mockito.`when`(xMassTreeRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(xMassTree))

        val foundTree = xMassTreeService.findById(1L)

        assertEquals(xMassTree, foundTree.get())
    }
}