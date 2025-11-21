package com.ecommercestarter.admin.presentation.products

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductEditScreen(
    productId: Int,
    onNavigateBack: () -> Unit,
    viewModel: ProductEditViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val name by viewModel.name.collectAsState()
    val description by viewModel.description.collectAsState()
    val price by viewModel.price.collectAsState()
    val stockQuantity by viewModel.stockQuantity.collectAsState()
    val imageUrl by viewModel.imageUrl.collectAsState()
    val category by viewModel.category.collectAsState()
    val isActive by viewModel.isActive.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // Handle state changes
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is ProductEditUiState.Success -> {
                snackbarHostState.showSnackbar("Product updated successfully!")
                onNavigateBack()
            }
            is ProductEditUiState.Error -> {
                snackbarHostState.showSnackbar(state.message)
                viewModel.clearError()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Product") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (uiState is ProductEditUiState.Loaded || uiState is ProductEditUiState.Saving) {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.updateProduct() },
                    text = { Text("Save Changes") },
                    icon = { },
                    expanded = true
                )
            }
        }
    ) { paddingValues ->
        when (uiState) {
            is ProductEditUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    val isLoading = uiState is ProductEditUiState.Saving
                    
                    // Name field
                    OutlinedTextField(
                        value = name,
                        onValueChange = { viewModel.onNameChange(it) },
                        label = { Text("Product Name *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = !isLoading
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Description field
                    OutlinedTextField(
                        value = description,
                        onValueChange = { viewModel.onDescriptionChange(it) },
                        label = { Text("Description") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        maxLines = 5,
                        enabled = !isLoading
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Price field
                    OutlinedTextField(
                        value = price,
                        onValueChange = { viewModel.onPriceChange(it) },
                        label = { Text("Price *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        prefix = { Text("$") },
                        enabled = !isLoading
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stock Quantity field
                    OutlinedTextField(
                        value = stockQuantity,
                        onValueChange = { viewModel.onStockQuantityChange(it) },
                        label = { Text("Stock Quantity *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        enabled = !isLoading
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Category field
                    OutlinedTextField(
                        value = category,
                        onValueChange = { viewModel.onCategoryChange(it) },
                        label = { Text("Category") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = !isLoading
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Image URL field
                    OutlinedTextField(
                        value = imageUrl,
                        onValueChange = { viewModel.onImageUrlChange(it) },
                        label = { Text("Image URL") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                        enabled = !isLoading
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Active toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Active",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                        Switch(
                            checked = isActive,
                            onCheckedChange = { viewModel.onIsActiveChange(it) },
                            enabled = !isLoading
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Loading indicator
                    if (isLoading) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }

                    Spacer(modifier = Modifier.height(80.dp)) // Space for FAB
                }
            }
        }
    }
}
