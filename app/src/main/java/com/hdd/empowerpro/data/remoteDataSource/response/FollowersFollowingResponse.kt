package com.hdd.empowerpro.data.remoteDataSource.response

import com.hdd.empowerpro.data.models.User


data class FollowersFollowingResponse(
    val success: Boolean? = null,
    val data: MutableList<User>? = null,
    val accessToken: String? = null,
    val message: String? = null
)