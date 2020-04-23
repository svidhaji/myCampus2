package com.example.testi.model

import java.util.*

data class LoginResponse (
    val token : String,
    val exp : Int,
    val username: String,
    val email: String,
    val roles : Array<String>,
    val errorResponse: PasswordReset
)
{
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LoginResponse

        if (!roles.contentEquals(other.roles)) return false

        return true
    }

    override fun hashCode(): Int {
        return roles.contentHashCode()
    }
}
