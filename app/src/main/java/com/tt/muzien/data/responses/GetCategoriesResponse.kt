package com.tt.muzien.data.responses


/**
 * Created by Faheem Abbas on 15/03/2025.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
data class GetCategoriesResponse(
    val status: Int,
    val message: String,
    val data: List<Category?>,
)

data class Category(
    val id: Int,
    val name: String,
    val nameAr: String,
    val image: String,
    val services: List<Service?>,
)
data class Service(
    val id: Long,
    val addedBy: Long,
    val saloonId: Long,
    val name: String,
    val duration: Long,
    val price: Long,
    val image: String,
)
