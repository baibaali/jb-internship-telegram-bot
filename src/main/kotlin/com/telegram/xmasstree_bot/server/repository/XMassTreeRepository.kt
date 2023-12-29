package com.telegram.xmasstree_bot.server.repository

import com.telegram.xmasstree_bot.server.entity.XMassTree
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository for XMassTree entity
 */
@Repository
interface XMassTreeRepository : JpaRepository<XMassTree, Long> {
}