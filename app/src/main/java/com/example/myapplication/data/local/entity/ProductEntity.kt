package com.example.myapplication.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    val image: String,
    val ratingRate: Double = 0.0,
    val ratingCount: Int = 0,
    @androidx.room.ColumnInfo(defaultValue = "0") val locallyModified: Boolean = false,
    @androidx.room.ColumnInfo(defaultValue = "0") val deleted: Boolean = false,
    @androidx.room.ColumnInfo(defaultValue = "0") val pendingSync: Boolean = false
)
