package com.telegram.xmasstree_bot.server.service.common

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*


/**
 * AbstractService class for entities' services.
 * @param R - repository class.
 * @param E - entity class.
 * @param ID - entity's id class.
 * @property repository - repository instance.
 */
abstract class AbstractEntityService<R : JpaRepository<E, ID>, E : Any, ID : Any>(private var repository: R) {

    /**
     * Find all entities.
     * @return list of entities.
     */
    fun findAll(): List<E> {
        return repository.findAll()
    }

    /**
     * Find all entities with pagination.
     * @param page - page number.
     * @param size - page size.
     * @return page of entities.
     */
    fun findAll(page: Int, size: Int): Page<E> {
        return repository.findAll(PageRequest.of(page, size))
    }

    /**
     * Find entity by id.
     * @param id - entity's id.
     * @return optional of entity.
     */
    fun findById(id: ID): Optional<E> {
        return repository.findById(id)
    }

    /**
     * Save entity to database.
     * @param entity - entity to save.
     * @return saved entity.
     */
    fun save(entity: E): E {
        return repository.save(entity)
    }

}
