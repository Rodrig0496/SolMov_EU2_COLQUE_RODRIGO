package com.example.myapplication.ui.addedit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.domain.model.Product
import com.example.myapplication.ui.catalog.CatalogViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductScreen(
    productId: Int,
    viewModel: CatalogViewModel,
    onBackClick: () -> Unit
) {
    val productFlow = remember(productId) { viewModel.getProductById(productId) }
    val existingProduct by productFlow.collectAsState(initial = null)

    var title by remember(existingProduct) { mutableStateOf(existingProduct?.title ?: "") }
    var price by remember(existingProduct) { mutableStateOf(existingProduct?.price?.toString() ?: "") }
    var description by remember(existingProduct) { mutableStateOf(existingProduct?.description ?: "") }
    var category by remember(existingProduct) { mutableStateOf(existingProduct?.category ?: "men's clothing") }
    var image by remember(existingProduct) { mutableStateOf(existingProduct?.image ?: "https://fakestoreapi.com/img/81fPKd-2AYL._AC_SL1500_.jpg") }

    var isError by remember { mutableStateOf(false) }

    val isEditing = productId != -1

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Editar Producto" else "Nuevo Producto") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título del Producto") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Precio ($)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Categoría (e.g. electronics, jewelery, men's clothing)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = image,
                onValueChange = { image = it },
                label = { Text("URL de la Imagen") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            if (isError) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Por favor, ingresa un título y precio válidos.",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val parsedPrice = price.toDoubleOrNull()
                    if (title.isBlank() || parsedPrice == null || !parsedPrice.isFinite() || parsedPrice <= 0) {
                        isError = true
                    } else {
                        isError = false
                        val updatedProduct = Product(
                            id = if (isEditing) productId else 0,
                            title = title.trim(),
                            price = parsedPrice,
                            description = description.trim(),
                            category = category.trim().lowercase(),
                            image = image.trim(),
                            ratingRate = existingProduct?.ratingRate ?: 4.5,
                            ratingCount = existingProduct?.ratingCount ?: 10
                        )
                        viewModel.addOrUpdateProduct(updatedProduct) {
                            onBackClick()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (isEditing) "Actualizar Producto" else "Guardar Producto",
                    fontSize = 16.sp
                )
            }
        }
    }
}
