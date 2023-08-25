package com.hdd.empowerpro.data.models

data class Post(
    var _id:String="",
    var user: User? = null,
    var postType:String? = null,
    var image: String? = null,
    var status:String? = null,
    var createdDate:String? = null,
    var updatedDate:String? = null,
    var relatedJob : Job? = null,
    var likes : MutableList<String>? = null,
    var commentCounter : Int? = null
    )