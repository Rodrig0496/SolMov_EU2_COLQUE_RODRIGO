package com.example.myapplication.data.remote.dto

import com.example.myapplication.data.local.entity.ProductEntity

data class ProductDto(
    val id: Int? = null,
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    val image: String,
    val rating: RatingDto? = null
)

data class RatingDto(
    val rate: Double = 0.0,
    val count: Int = 0
)

fun ProductDto.toEntity(): ProductEntity {
    return ProductEntity(
        id = id ?: 0,
        title = title,
        price = price,
        description = description,
        category = category,
        image = image,
        ratingRate = rating?.rate ?: 0.0,
        ratingCount = rating?.count ?: 0
    )
}
