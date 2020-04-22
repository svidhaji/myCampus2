package com.example.testi.model

import com.google.gson.annotations.SerializedName

data class PasswordReset(
    @SerializedName("error")
    val error: List<Msg>
)

data class Msg (
    @SerializedName("message")
    val message: String
)