package org.jetbrains.chat.kmp.services

import org.koin.dsl.module

val servicesModule = module {
    single { ChatService() }
    single { UserService() }
    single { AIService() }
}
