package com.example.testi.model

data class UserLogin(
    val email : String,
    val password: String
)

data class UserReg (
    val email: String,
    val name: String,
    val password: String
)

data class UserEmail (val email: String)

data class Validate(
    val email: String,
    val token: String
)

data class ResetPass(
    val email: String,
    val password: String,
    val passwordConfirm: String,
    val resetToken: String
)
