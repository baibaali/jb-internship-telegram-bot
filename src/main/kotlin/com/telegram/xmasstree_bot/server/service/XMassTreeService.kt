package com.telegram.xmasstree_bot.server.service

import com.telegram.xmasstree_bot.server.entity.XMassTree
import com.telegram.xmasstree_bot.server.repository.XMassTreeRepository
import org.springframework.stereotype.Service

/**
 * XMassTreeService class.
 */
@Service
class XMassTreeService(private val repository: XMassTreeRepository) {

    fun save(tree: XMassTree) = repository.save(tree)

    fun findAll() = repository.findAll()

    fun findById(id: Long) = repository.findById(id)
}