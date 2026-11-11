package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.myapplication.data.local.database.AppDatabase
import com.example.myapplication.data.remote.RetrofitInstance
import com.example.myapplication.data.repository.ProductRepositoryImpl
import com.example.myapplication.ui.catalog.CatalogViewModel
import com.example.myapplication.ui.navigation.NavGraph
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getInstance(applicationContext)
        val apiService = RetrofitInstance.api
        com.example.myapplication.data.sync.CatalogSyncWorker.start(applicationContext)
        val repository = ProductRepositoryImpl(database.productDao(), apiService) {
            com.example.myapplication.data.sync.CatalogSyncWorker.enqueue(applicationContext)
        }
        val viewModelFactory = CatalogViewModel.Factory(repository)
        val viewModel: CatalogViewModel by viewModels { viewModelFactory }

        setContent {
            MyApplicationTheme {
                NavGraph(viewModel = viewModel)
            }
        }
    }
}
