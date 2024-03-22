package org.jetbrains.chat.kmp

import app.cash.sqldelight.db.SqlDriver

expect class DriverFactory {
    fun createDriver(): SqlDriver
}

fun createDatabase(driverFactory: DriverFactory): DatabaseQueries {
    val driver = driverFactory.createDriver()
    val database = Database(driver)
    
    return database.databaseQueries
}
