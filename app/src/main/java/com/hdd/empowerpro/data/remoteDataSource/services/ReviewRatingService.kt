package com.hdd.empowerpro.data.remoteDataSource.services

import com.hdd.empowerpro.data.models.ReviewRating
import com.hdd.empowerpro.data.remoteDataSource.response.ReviewRatingResponse
import retrofit2.Response
import retrofit2.http.*

interface ReviewRatingService {
    @POST("review/{id}")
    suspend fun addReviewRating(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body review: ReviewRating
    ): Response<ReviewRatingResponse>

    @GET("review/get/{jobId}")
    suspend fun getAllReviewRating(
        @Path("jobId") jobId: String,
    ): Response<ReviewRatingResponse>

    @PATCH("review/{jobId}")
    suspend fun updateReviewRating(
        @Header("Authorization") token: String,
        @Path("jobId") jobId: String,
        @Body review: ReviewRating
    ): Response<ReviewRatingResponse>

    @DELETE("review/{jobId}/{reviewId}")
    suspend fun deleteReviewRating(
        @Header("Authorization") token: String,
        @Path("jobId") jobId: String,
        @Path("reviewId") reviewId: String,
    ): Response<ReviewRatingResponse>

}