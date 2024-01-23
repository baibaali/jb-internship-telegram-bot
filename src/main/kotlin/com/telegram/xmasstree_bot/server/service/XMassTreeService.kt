package com.telegram.xmasstree_bot.server.service

import com.telegram.xmasstree_bot.server.entity.XMassTree
import com.telegram.xmasstree_bot.server.repository.XMassTreeRepository
import com.telegram.xmasstree_bot.server.service.common.AbstractEntityService
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service

/**
 * XMassTreeService class.
 */
@Service
class XMassTreeService(repository: XMassTreeRepository):
        AbstractEntityService<JpaRepository<XMassTree, Long>, XMassTree, Long>(repository)