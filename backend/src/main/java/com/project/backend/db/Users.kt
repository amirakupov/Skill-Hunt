package com.project.backend.db

import org.jetbrains.exposed.dao.id.LongIdTable

object Users : LongIdTable("users") {
    val email    = varchar("email", 255).uniqueIndex()
    val password = varchar("password", 60)   // bcrypt hashes are ~60 chars
}
