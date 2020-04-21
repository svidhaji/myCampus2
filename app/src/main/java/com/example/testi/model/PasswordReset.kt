package com.example.testi.model

data class PasswordReset(
    val errors : List<Msg>
)

data class Msg(
    val message: String
)