package com.example.myapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.ui.addedit.AddEditProductScreen
import com.example.myapplication.ui.catalog.CatalogScreen
import com.example.myapplication.ui.catalog.CatalogViewModel
import com.example.myapplication.ui.detail.ProductDetailScreen

@Composable
fun NavGraph(
    viewModel: CatalogViewModel,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Catalog.route
    ) {
        composable(route = Screen.Catalog.route) {
            CatalogScreen(
                viewModel = viewModel,
                onProductClick = { productId ->
                    navController.navigate(Screen.Detail.createRoute(productId))
                },
                onAddProductClick = {
                    navController.navigate(Screen.AddEdit.createRoute())
                }
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: 0
            ProductDetailScreen(
                productId = productId,
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onEditClick = { idToEdit ->
                    navController.navigate(Screen.AddEdit.createRoute(idToEdit))
                }
            )
        }

        composable(
            route = Screen.AddEdit.route,
            arguments = listOf(navArgument("productId") {
                type = NavType.IntType
                defaultValue = -1
            })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: -1
            AddEditProductScreen(
                productId = productId,
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
