package com.example.testi.model

import java.util.*

class LoginResponse (
    val token : String,
    val exp : Int,
    val username: String,
    val email: String,
    val roles : Array<String>,
    val errors: List<PasswordReset>
)

