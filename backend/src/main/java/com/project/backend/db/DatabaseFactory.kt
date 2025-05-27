package com.project.backend.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        val config = HikariConfig().apply {
            jdbcUrl = "jdbc:postgresql://localhost:5432/skillhunt?user=skilluser&password=blabla"
            driverClassName = "org.postgresql.Driver"
            username = "skilluser"
            password = "blabla"
            maximumPoolSize = 10
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        }
        val ds = HikariDataSource(config)
        Database.connect(ds)

        // Create tables at startup
        transaction {
            SchemaUtils.create(Users, Courses)
        }
    }
}
