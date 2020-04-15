package com.example.testi.model

import retrofit2.Call
import retrofit2.http.*

/**
 * Interface for defining REST request functions
 */

interface ApiService {

    @POST("auth/login")
    @Headers("Content-Type: application/json")
    fun loginUser(
        @Body userL: UserLogin
    ): Call<LoginResponse>

    @POST("auth/signup")
    @Headers("Content-Type: application/json")
    fun createUser(
        @Body userR: UserReg
    ):Call<SignUpResponse>
}