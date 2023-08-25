package com.hdd.empowerpro.repository

import com.hdd.empowerpro.data.models.ReviewRating
import com.hdd.empowerpro.data.remoteDataSource.HttpRequestNetworkCall
import com.hdd.empowerpro.data.remoteDataSource.ServiceBuilder
import com.hdd.empowerpro.data.remoteDataSource.response.ReviewRatingResponse
import com.hdd.empowerpro.data.remoteDataSource.services.ReviewRatingService


class ReviewRatingRepository : HttpRequestNetworkCall() {
    private val reviewService = ServiceBuilder.buildService(ReviewRatingService::class.java)

    suspend fun getAllReviewRating(jobId:String): ReviewRatingResponse {
        return myHttpRequestNetworkCall {
            reviewService.getAllReviewRating(jobId)
        }
    }

    suspend fun addReviewRating(reviewId:String,review: ReviewRating): ReviewRatingResponse {
        return myHttpRequestNetworkCall {
            reviewService.addReviewRating(ServiceBuilder.token!!,reviewId,review)
        }
    }

    suspend fun updateReviewRating(jobId:String,review:ReviewRating): ReviewRatingResponse {
        return myHttpRequestNetworkCall {
            reviewService.updateReviewRating(ServiceBuilder.token!!,jobId,review)
        }
    }

    suspend fun deleteReviewRating(jobId:String,reviewId:String): ReviewRatingResponse {
        return myHttpRequestNetworkCall {
            reviewService.deleteReviewRating(ServiceBuilder.token!!,jobId,reviewId)
        }
    }
}