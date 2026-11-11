package com.example.myapplication.domain.model

import com.example.myapplication.data.local.entity.ProductEntity
import com.example.myapplication.data.remote.dto.ProductDto
import com.example.myapplication.data.remote.dto.RatingDto

data class Product(
    val id: Int = 0,
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    val image: String,
    val ratingRate: Double = 0.0,
    val ratingCount: Int = 0
)

fun ProductEntity.toDomain(): Product {
    return Product(
        id = id,
        title = title,
        price = price,
        description = description,
        category = category,
        image = image,
        ratingRate = ratingRate,
        ratingCount = ratingCount
    )
}

fun Product.toEntity(): ProductEntity {
    return ProductEntity(
        id = id,
        title = title,
        price = price,
        description = description,
        category = category,
        image = image,
        ratingRate = ratingRate,
        ratingCount = ratingCount
    )
}

fun Product.toDto(): ProductDto {
    return ProductDto(
        id = if (id == 0) null else id,
        title = title,
        price = price,
        description = description,
        category = category,
        image = image,
        rating = RatingDto(rate = ratingRate, count = ratingCount)
    )
}
