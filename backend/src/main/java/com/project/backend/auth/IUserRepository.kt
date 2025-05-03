package com.project.backend.auth

interface IUserRepository {
    fun add(email: String, hash: String)
    fun exists(email: String): Boolean
    fun verify(email: String, plain: String): Boolean
}
