package org.jetbrains.chat.kmp

import app.cash.sqldelight.db.SqlDriver

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        error("")
    }
}
