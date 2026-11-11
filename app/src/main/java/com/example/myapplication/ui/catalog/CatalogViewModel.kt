package com.example.myapplication.ui.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.model.Product
import com.example.myapplication.domain.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CatalogUiState(
    val products: List<Product> = emptyList(),
    val categories: List<String> = emptyList(),
    val selectedCategory: String? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isOffline: Boolean = false,
    val errorMessage: String? = null
)

class CatalogViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow<String?>(null)
    private val _isRefreshing = MutableStateFlow(false)
    private val _isOffline = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)

    private val catalogState = combine(
        repository.getProducts(),
        _searchQuery,
        _selectedCategory,
        _isRefreshing,
        _isOffline
    ) { products: List<Product>, query: String, category: String?, isRefreshing: Boolean, isOffline: Boolean ->
        val categories = products.map { it.category }.distinct()
        val filtered = products.filter { product ->
            val matchesQuery = query.isBlank() || product.title.contains(query, ignoreCase = true) || product.category.contains(query, ignoreCase = true)
            val matchesCategory = category == null || product.category.equals(category, ignoreCase = true)
            matchesQuery && matchesCategory
        }

        CatalogUiState(
            products = filtered,
            categories = categories,
            selectedCategory = category,
            searchQuery = query,
            isLoading = isRefreshing && products.isEmpty(),
            isRefreshing = isRefreshing,
            isOffline = isOffline,
            errorMessage = null
        )
    }

    val uiState: StateFlow<CatalogUiState> = combine(catalogState, _errorMessage) { state, error ->
        state.copy(errorMessage = error)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CatalogUiState(isLoading = true)
    )

    init {
        refresh()
    }

    fun getProductById(id: Int) = repository.getProductById(id)

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelected(category: String?) {
        _selectedCategory.value = category
    }

    fun refresh() {
        if (_isRefreshing.value) return
        viewModelScope.launch {
            _isRefreshing.value = true
            _errorMessage.value = null
            val result = repository.refreshProducts()
            if (result.isFailure) {
                _isOffline.value = true
                _errorMessage.value = "Modo Offline: Mostrando catálogo desde la BD local (Room SSOT)"
            } else {
                _isOffline.value = false
            }
            _isRefreshing.value = false
        }
    }

    fun deleteProduct(id: Int) {
        viewModelScope.launch {
            repository.deleteProduct(id)
        }
    }

    fun addOrUpdateProduct(product: Product, onComplete: () -> Unit) {
        viewModelScope.launch {
            val result = if (product.id == 0) {
                repository.addProduct(product)
            } else {
                repository.updateProduct(product)
            }
            if (result.isSuccess) onComplete()
            else _errorMessage.value = "No se pudo guardar el producto en el dispositivo."
        }
    }

    class Factory(private val repository: ProductRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CatalogViewModel(repository) as T
        }
    }
}
