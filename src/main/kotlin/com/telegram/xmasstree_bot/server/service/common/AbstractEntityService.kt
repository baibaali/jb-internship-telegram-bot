package com.telegram.xmasstree_bot.server.service.common

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*


abstract class AbstractEntityService<R : JpaRepository<E, ID>, E : Any, ID : Any>(private var repository: R) {

    fun findAll(): List<E> {
        return repository.findAll()
    }

    fun findAll(page: Int, size: Int): Page<E> {
        return repository.findAll(PageRequest.of(page, size))
    }

    fun findById(id: ID): Optional<E> {
        return repository.findById(id)
    }

    fun save(entity: E): E {
        return repository.save(entity)
    }

}
