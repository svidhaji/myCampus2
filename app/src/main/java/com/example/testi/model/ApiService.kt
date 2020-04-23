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

    @POST("auth/forgot_password")
    @Headers("Content-Type: application/json")
    fun resetPass(
        @Body email : UserEmail
    ): Call<PasswordReset>

    @POST("auth/reset_password")
    @Headers("Content-Type: application/json")
    fun reset(
        @Body reset : ResetPass
    ): Call<PasswordReset>

    @POST("auth/confirmation")
    @Headers("Content-Type: application/json")
    fun validate(
        @Body validate : Validate
    ): Call<LoginResponse>
}