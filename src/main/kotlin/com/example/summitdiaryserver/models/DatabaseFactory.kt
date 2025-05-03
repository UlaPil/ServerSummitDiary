
package com.example.summitdiaryserver.models

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.Database

object DatabaseFactory {
    fun init() {
        Database.connect("jdbc:sqlite:./summitdiary.db", driver = "org.sqlite.JDBC")
        transaction {
            SchemaUtils.create(UserTable, HikeTable, PhotoTable, HikePhotoTable)
        }
    }
}
