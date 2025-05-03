package com.project.backend.auth

import com.project.backend.db.Users
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import at.favre.lib.crypto.bcrypt.BCrypt

object UserRepositoryImpl : IUserRepository {
    override fun add(email: String, hash: String) {
        transaction {
            Users.insert {
                it[Users.email]    = email
                it[Users.password] = hash
            }
        }
    }

    override fun exists(email: String): Boolean = transaction {
        Users.select { Users.email eq email }
            .empty().not()
    }

    override fun verify(email: String, plain: String): Boolean = transaction {
        Users.select { Users.email eq email }
            .map { it[Users.password] }
            .singleOrNull()
            ?.let { storedHash ->
                BCrypt.verifyer()
                    .verify(plain.toCharArray(), storedHash)
                    .verified
            } ?: false
    }
}


