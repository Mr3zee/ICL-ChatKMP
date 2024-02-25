package org.jetbrains.chat.kmp.services

import org.jetbrains.chat.kmp.db.DB
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class UserService : KoinComponent {
    private val db by inject<DB>()
    
    suspend fun loginUser(userId: Long): Boolean {
        return db.loginUser(userId)
    }

    suspend fun createUser(): Long {
        return db.createUser()
    }
}
