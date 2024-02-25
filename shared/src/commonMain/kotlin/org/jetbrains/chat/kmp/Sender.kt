package org.jetbrains.chat.kmp

import kotlinx.serialization.Serializable

@Serializable
enum class Sender {
    Assistant, System, User;
}
