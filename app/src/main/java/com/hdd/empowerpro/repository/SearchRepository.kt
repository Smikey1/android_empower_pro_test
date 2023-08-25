package com.hdd.empowerpro.repository

import com.hdd.empowerpro.data.models.Search
import com.hdd.empowerpro.data.remoteDataSource.HttpRequestNetworkCall
import com.hdd.empowerpro.data.remoteDataSource.ServiceBuilder
import com.hdd.empowerpro.data.remoteDataSource.response.SearchResponse
import com.hdd.empowerpro.data.remoteDataSource.services.SearchServices


class SearchRepository : HttpRequestNetworkCall() {
    private val searchService = ServiceBuilder.buildService(SearchServices::class.java)
    
    suspend fun search(search: Search): SearchResponse {
        return myHttpRequestNetworkCall {
            searchService.search(search)
        }
    }
}