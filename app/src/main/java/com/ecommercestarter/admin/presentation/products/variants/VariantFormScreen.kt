package com.ecommercestarter.admin.presentation.products.variants

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VariantFormScreen(
    productId: Int,
    variantId: Int? = null,
    productName: String,
    onNavigateBack: () -> Unit,
    viewModel: VariantFormViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val formState by viewModel.formState.collectAsState()
    val saveState by viewModel.saveState.collectAsState()
    val showDeleteDialog = remember { mutableStateOf(false) }

    LaunchedEffect(productId, variantId) {
        if (variantId != null) {
            viewModel.loadVariant(productId, variantId)
        }
    }

    LaunchedEffect(saveState) {
        when (saveState) {
            is VariantSaveState.Success -> {
                Toast.makeText(
                    context,
                    if (variantId == null) "Variant created" else "Variant updated",
                    Toast.LENGTH_SHORT
                ).show()
                onNavigateBack()
            }
            is VariantSaveState.Error -> {
                Toast.makeText(
                    context,
                    (saveState as VariantSaveState.Error).message,
                    Toast.LENGTH_LONG
                ).show()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            if (variantId == null) "Create Variant" else "Edit Variant",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            productName,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (variantId != null) {
                        IconButton(onClick = { showDeleteDialog.value = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                    IconButton(
                        onClick = { viewModel.saveVariant(productId, variantId) },
                        enabled = saveState !is VariantSaveState.Saving
                    ) {
                        if (saveState is VariantSaveState.Saving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Icon(Icons.Default.Check, contentDescription = "Save")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        when (formState) {
            is VariantFormState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is VariantFormState.Ready -> {
                val state = formState as VariantFormState.Ready
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = state.name,
                        onValueChange = { viewModel.updateName(it) },
                        label = { Text("Variant Name *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = state.nameError != null,
                        supportingText = state.nameError?.let { { Text(it) } }
                    )

                    OutlinedTextField(
                        value = state.sku,
                        onValueChange = { viewModel.updateSku(it) },
                        label = { Text("SKU") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = state.stockQuantity,
                        onValueChange = { viewModel.updateStockQuantity(it) },
                        label = { Text("Stock Quantity *") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        isError = state.stockError != null,
                        supportingText = state.stockError?.let { { Text(it) } }
                    )

                    OutlinedTextField(
                        value = state.priceOverride,
                        onValueChange = { viewModel.updatePriceOverride(it) },
                        label = { Text("Price Override") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        isError = state.priceError != null,
                        supportingText = state.priceError?.let { { Text(it) } },
                        placeholder = { Text("Leave empty to use product price") }
                    )

                    OutlinedTextField(
                        value = state.imageUrl,
                        onValueChange = { viewModel.updateImageUrl(it) },
                        label = { Text("Image URL") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = state.displayOrder,
                        onValueChange = { viewModel.updateDisplayOrder(it) },
                        label = { Text("Display Order") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        supportingText = { Text("Lower numbers appear first") }
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                "Options",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Available",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        "Variant is available for purchase",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Switch(
                                    checked = state.isAvailable,
                                    onCheckedChange = { viewModel.updateIsAvailable(it) }
                                )
                            }

                            Divider()

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Featured",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        "Highlight this variant (only one per product)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Switch(
                                    checked = state.isFeatured,
                                    onCheckedChange = { viewModel.updateIsFeatured(it) }
                                )
                            }
                        }
                    }

                    Text(
                        "* Required fields",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            is VariantFormState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            (formState as VariantFormState.Error).message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(onClick = onNavigateBack) {
                            Text("Go Back")
                        }
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog.value && variantId != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            title = { Text("Delete Variant") },
            text = { Text("Are you sure you want to delete this variant? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog.value = false
                        viewModel.deleteVariant(
                            productId = productId,
                            variantId = variantId,
                            onSuccess = {
                                Toast.makeText(context, "Variant deleted", Toast.LENGTH_SHORT).show()
                                onNavigateBack()
                            },
                            onError = { message ->
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
