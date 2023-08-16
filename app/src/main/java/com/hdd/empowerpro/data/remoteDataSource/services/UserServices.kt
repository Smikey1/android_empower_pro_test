package com.hdd.empowerpro.data.remoteDataSource.services

import com.hdd.empowerpro.data.models.User
import com.hdd.empowerpro.data.remoteDataSource.response.ImageResponse
import com.hdd.empowerpro.data.remoteDataSource.response.UserResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface UserServices {
    @POST("user/register")
    suspend fun registerUser(
        @Body user: User,
    ): Response<UserResponse>

    @POST("user/login")
    suspend fun login(@Body user: User): Response<UserResponse>

    @GET("user")
    suspend fun getUserProfile(
        @Header("Authorization") token: String,
    ):Response<UserResponse>

    @POST("user/resend-login-otp")
    suspend fun resendLoginOTP(
        @Header("Authorization") token: String,
    ):Response<UserResponse>


    @PATCH("user")
    suspend fun updateUserProfile(
        @Header("Authorization") token: String,
        @Body user: User
    ):Response<UserResponse>

    @PATCH("user/email-phone")
    suspend fun updateUserEmailOrPhone(
        @Header("Authorization") token: String,
        @Body user: User
    ):Response<UserResponse>

    @PATCH("user/device")
    suspend fun updateUserDevice(
        @Header("Authorization") token: String,
        @Body user: User
    ):Response<UserResponse>


    @Multipart
    @PATCH("user/profile")
    suspend fun uploadImage(
        @Header("Authorization") token: String,
        @Part profile: MultipartBody.Part
    ): Response<ImageResponse>

    @PATCH("user/password")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Body user: User,
    ):Response<UserResponse>


    @POST("user/reset-user-code-for-password")
    suspend fun resetUserCodeForResetPassword(
        @Body user: User,
    ): Response<UserResponse>

    @POST("user/reset-user-code-for-email-phone")
    suspend fun resetUserCodeForUpdateEmailPhone(
        @Header("Authorization") token: String,
        @Body user: User,
    ): Response<UserResponse>

    @PATCH("user/new-password")
    suspend fun setNewPassword(
        @Body user: User,
    ): Response<UserResponse>

    @POST("user/validate-email")
    suspend fun validateEmail(
        @Body user: User,
    ): Response<UserResponse>

}
