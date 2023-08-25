package com.hdd.empowerpro.data.remoteDataSource.services

import com.hdd.empowerpro.data.models.Post
import com.hdd.empowerpro.data.remoteDataSource.response.PostResponse
import com.hdd.empowerpro.data.remoteDataSource.response.PostsResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface PostServices {
    @POST("post/status")
    suspend fun addPostWithoutImage(
        @Header("Authorization") token: String,
        @Body post: Post,
    ): Response<PostResponse>

    @Multipart
    @POST("post")
    suspend fun addPostWithImage(
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part,
        @Part("status") status: RequestBody,
    ): Response<PostResponse>

    @GET("post")
    suspend fun getAllPost(): Response<PostsResponse>

    @GET("post/getFollowingPost")
    suspend fun getFollowingPost(@Header("Authorization") token: String): Response<PostsResponse>

    @GET("post/trending")
    suspend fun getTrendingPost(): Response<PostsResponse>

    @GET("post/all-created")
    suspend fun getAllCreatedPost(@Header("Authorization") token: String): Response<PostsResponse>

    @PATCH("post/status/{id}")
    suspend fun updatePostWithoutImage(
        @Header("Authorization") token: String,
        @Path("id") id:String,
        @Body post: Post,
    ): Response<PostResponse>

    @Multipart
    @PATCH("post/{id}")
    suspend fun updatePostWithImage(
        @Header("Authorization") token: String,
        @Path("id") id:String,
        @Part image: MultipartBody.Part,
        @Part("status") status: RequestBody,
    ): Response<PostResponse>

    @GET("post/single/{id}")
    suspend fun getPostById(@Path("id") id:String ): Response<PostResponse>

    @GET("post/archive")
    suspend fun viewArchivedPost(
        @Header("Authorization") token: String,
    ): Response<PostsResponse>

    @POST("post/archivePost/{id}")
    suspend fun archivePost(
        @Header("Authorization") token: String,
        @Path("id") id: String,
    ): Response<PostResponse>

    @DELETE("post/deleteArchived/{id}")
    suspend fun deleteArchived(
        @Header("Authorization") token: String,
        @Path("id") id: String,
    ): Response<PostResponse>

    @DELETE("post/deletePost/{id}")
    suspend fun deletePost(
        @Header("Authorization") token: String,
        @Path("id") id: String,
    ): Response<PostResponse>

    @FormUrlEncoded
    @POST("reportPost/{id}")
    suspend fun reportPost(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Field("reason") reason:String
    ): Response<PostResponse>
}
