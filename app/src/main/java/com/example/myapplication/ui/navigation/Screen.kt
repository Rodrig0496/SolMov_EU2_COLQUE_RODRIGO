package com.example.myapplication.ui.navigation

sealed class Screen(val route: String) {
    object Catalog : Screen("catalog")
    object Detail : Screen("detail/{productId}") {
        fun createRoute(productId: Int) = "detail/$productId"
    }
    object AddEdit : Screen("add_edit?productId={productId}") {
        fun createRoute(productId: Int = -1) = "add_edit?productId=$productId"
    }
}
