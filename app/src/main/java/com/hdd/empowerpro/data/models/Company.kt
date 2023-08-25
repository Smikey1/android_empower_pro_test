package com.hdd.empowerpro.data.models

data class Company(
    var _id:String="",
    var name:String? = null,
    var description:String? = null,
    var phone:String? = null,
    var openingTime:String? = null,
    var closingTime:String? = null,
    var address:String? = null,
    var image: String? = null,
    var coverImage:String? = null,
//    var review:MutableList<ReviewRating>?=null
)