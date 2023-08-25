package com.hdd.empowerpro.data.models


data class ReviewRating(
    val _id:String="",
    val review:String?=null,
    val rating:Int=0,
    val date: String?=null,
    val job:String?=null,
    val user: User?=null,
)
