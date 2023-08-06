package com.hdd.empowerpro.data.remoteDataSource.response

import com.hdd.empowerpro.data.models.User


data class UserResponse(
    val success: Boolean? = null,
    val data: User? = null,
    val accessToken: String? = null,
    val accessCode: String? = null,
    val message: String? = null
)