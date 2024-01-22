package com.telegram.xmasstree_bot.server.service

import com.telegram.xmasstree_bot.server.entity.User
import com.telegram.xmasstree_bot.server.repository.UserRepository
import com.telegram.xmasstree_bot.server.service.common.AbstractEntityService
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service

@Service
class UserService(userRepository: UserRepository):
    AbstractEntityService<JpaRepository<User, Long>, User, Long>(userRepository)