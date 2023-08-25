package com.hdd.empowerpro.data.remoteDataSource.response

import com.hdd.empowerpro.data.models.ReviewRating


data class ReviewRatingResponse(
    val success: Boolean? = null,
    val data: MutableList<ReviewRating>? = null,
)