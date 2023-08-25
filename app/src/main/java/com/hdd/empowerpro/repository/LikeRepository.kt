package com.hdd.pakwan.repository

import com.hdd.empowerpro.data.remoteDataSource.HttpRequestNetworkCall
import com.hdd.empowerpro.data.remoteDataSource.ServiceBuilder
import com.hdd.empowerpro.data.remoteDataSource.services.LikeServices
import com.hdd.pakwan.data.remoteDataSource.response.LikeResponse


class LikeRepository : HttpRequestNetworkCall() {
    private val likeService = ServiceBuilder.buildService(LikeServices::class.java)

    suspend fun updateLike(postId:String): LikeResponse {
        return myHttpRequestNetworkCall {
            likeService.updateLike(ServiceBuilder.token!!,postId)
        }
    }
}

