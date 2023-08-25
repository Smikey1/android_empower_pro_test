package com.hdd.empowerpro.repository

import com.hdd.empowerpro.data.models.Post
import com.hdd.empowerpro.data.remoteDataSource.HttpRequestNetworkCall
import com.hdd.empowerpro.data.remoteDataSource.ServiceBuilder
import com.hdd.empowerpro.data.remoteDataSource.response.PostResponse
import com.hdd.empowerpro.data.remoteDataSource.response.PostsResponse
import com.hdd.empowerpro.data.remoteDataSource.services.PostServices
import okhttp3.MultipartBody
import okhttp3.RequestBody


class PostRepository : HttpRequestNetworkCall() {
    private val postService = ServiceBuilder.buildService(PostServices::class.java)

    suspend fun addPostWithImage(status: RequestBody, image: MultipartBody.Part): PostResponse {
        return myHttpRequestNetworkCall {
            postService.addPostWithImage(ServiceBuilder.token!!, image, status)
        }
    }
    suspend fun addPostWithoutImage(post: Post): PostResponse {
        return myHttpRequestNetworkCall {
            postService.addPostWithoutImage(ServiceBuilder.token!!, post)
        }
    }
    suspend fun getAllPost(): PostsResponse {
        return myHttpRequestNetworkCall {
            postService.getAllPost()
        }
    }
    suspend fun getFollowingPost(): PostsResponse {
        return myHttpRequestNetworkCall {
            postService.getFollowingPost(ServiceBuilder.token!!)
        }
    }

    suspend fun getTrendingPost(): PostsResponse {
        return myHttpRequestNetworkCall {
            postService.getTrendingPost()
        }
    }
    suspend fun getAllCreatedPost(): PostsResponse {
        return myHttpRequestNetworkCall {
            postService.getAllCreatedPost(ServiceBuilder.token!!)
        }
    }
    suspend fun updatePostWithImage(postId: String,status: RequestBody, image: MultipartBody.Part): PostResponse {
        return myHttpRequestNetworkCall {
            postService.updatePostWithImage(ServiceBuilder.token!!,postId,image, status)
        }
    }
    suspend fun updatePostWithoutImage(postId: String,post: Post): PostResponse {
        return myHttpRequestNetworkCall {
            postService.updatePostWithoutImage(ServiceBuilder.token!!,postId, post)
        }
    }
    suspend fun getPostById(postId:String): PostResponse {
        return myHttpRequestNetworkCall {
            postService.getPostById(postId)
        }
    }

    suspend fun viewArchivedPost(): PostsResponse {
        return myHttpRequestNetworkCall {
            postService.viewArchivedPost(ServiceBuilder.token!!)
        }
    }

    suspend fun archivePost(postId: String): PostResponse {
        return myHttpRequestNetworkCall {
            postService.archivePost(ServiceBuilder.token!!, postId  )
        }
    }

    suspend fun deleteArchived(postId: String): PostResponse {
        return myHttpRequestNetworkCall {
            postService.deleteArchived(ServiceBuilder.token!!, postId  )
        }
    }

    suspend fun deletePost(postId: String): PostResponse {
        return myHttpRequestNetworkCall {
            postService.deletePost(ServiceBuilder.token!!, postId  )
        }
    }

    suspend fun reportPost(postId: String,reportReason:String): PostResponse {
        return myHttpRequestNetworkCall {
            postService.reportPost(ServiceBuilder.token!!, postId,reportReason)
        }
    }
}