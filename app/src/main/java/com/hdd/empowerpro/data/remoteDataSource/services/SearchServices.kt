package com.hdd.empowerpro.data.remoteDataSource.services

import com.hdd.empowerpro.data.models.Search
import com.hdd.empowerpro.data.remoteDataSource.response.SearchResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SearchServices {
    @POST("search")
    suspend fun search(
        @Body search: Search,
    ): Response<SearchResponse>
}
