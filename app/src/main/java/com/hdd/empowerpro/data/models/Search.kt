package com.hdd.empowerpro.data.models

data class Search(
    var pattern: String? = null,
    var user: MutableList<User>? = null,
    var job : MutableList<Job>? = null,
)