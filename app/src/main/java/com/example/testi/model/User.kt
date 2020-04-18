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
