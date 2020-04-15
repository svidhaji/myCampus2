package com.example.testi.model

data class SignUpResponse(
    val error : Error
)


data class Message(
    val msg : String
)

data class Error (
    val errors : List<Message>
)