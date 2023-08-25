package com.hdd.empowerpro.data.remoteDataSource.services

import com.hdd.pakwan.data.remoteDataSource.response.LikeResponse
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.Path

interface LikeServices {
    @PATCH("post/like/{id}")
    suspend fun updateLike(
        @Header("Authorization") token: String,
        @Path("id") id: String,
    ): Response<LikeResponse>

}

