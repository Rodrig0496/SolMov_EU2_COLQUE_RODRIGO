package com.example.myapplication.data.remote.api

import com.example.myapplication.data.remote.dto.ProductDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface FakeStoreApiService {
    @GET("products")
    suspend fun getProducts(): List<ProductDto>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: Int): ProductDto

    @POST("products")
    suspend fun addProduct(@Body product: ProductDto): ProductDto

    @PUT("products/{id}")
    suspend fun updateProduct(@Path("id") id: Int, @Body product: ProductDto): ProductDto

    @DELETE("products/{id}")
    suspend fun deleteProduct(@Path("id") id: Int): ProductDto
}
