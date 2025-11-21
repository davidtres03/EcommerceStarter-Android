package com.ecommercestarter.admin.presentation.products.categories

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFormScreen(
    categoryId: Int?,
    onNavigateBack: () -> Unit,
    viewModel: CategoryViewModel = hiltViewModel()
) {
    val formState by viewModel.formState.collectAsState()
    val operationState by viewModel.operationState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val isEditMode = categoryId != null
    
    // Load category for editing
    LaunchedEffect(categoryId) {
        if (categoryId != null) {
            // Load category data - would need to add getCategory to ViewModel
            // For now, form starts empty even in edit mode
            // You can enhance this by loading the category data
        }
    }
    
    // Handle operation result
    LaunchedEffect(operationState) {
        when (val state = operationState) {
            is OperationState.Success -> {
                snackbarHostState.showSnackbar(state.message)
                onNavigateBack()
            }
            is OperationState.Error -> {
                snackbarHostState.showSnackbar(state.message)
                viewModel.resetOperationState()
            }
            else -> {}
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        if (isEditMode) "Edit Category" else "New Category",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (isEditMode && categoryId != null) {
                        viewModel.updateCategory(categoryId)
                    } else {
                        viewModel.createCategory()
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Save, contentDescription = "Save")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Name field
            OutlinedTextField(
                value = formState.name,
                onValueChange = viewModel::onNameChange,
                label = { Text("Category Name *") },
                placeholder = { Text("e.g., Apparel, Electronics") },
                isError = formState.nameError != null,
                supportingText = {
                    val errorText = formState.nameError
                    if (errorText != null) {
                        Text(errorText)
                    }
                },
                leadingIcon = {
                    Icon(Icons.Default.Category, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // Description field
            OutlinedTextField(
                value = formState.description,
                onValueChange = viewModel::onDescriptionChange,
                label = { Text("Description") },
                placeholder = { Text("Optional description for this category") },
                leadingIcon = {
                    Icon(Icons.Default.Description, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )
            
            // Icon Class field
            OutlinedTextField(
                value = formState.iconClass,
                onValueChange = viewModel::onIconClassChange,
                label = { Text("Icon Class") },
                placeholder = { Text("bi-tag, bi-shirt, bi-phone") },
                leadingIcon = {
                    Icon(Icons.Default.Palette, contentDescription = null)
                },
                supportingText = {
                    Text("Bootstrap icon class (e.g., bi-tag, bi-basket)")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // Display Order field
            OutlinedTextField(
                value = formState.displayOrder,
                onValueChange = viewModel::onDisplayOrderChange,
                label = { Text("Display Order") },
                placeholder = { Text("0") },
                leadingIcon = {
                    Icon(Icons.Default.FormatListNumbered, contentDescription = null)
                },
                supportingText = {
                    Text("Lower numbers appear first")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // Enabled switch
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Enable Category",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Disabled categories are hidden from customers",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = formState.isEnabled,
                        onCheckedChange = viewModel::onIsEnabledChange
                    )
                }
            }
            
            // Loading indicator
            if (operationState is OperationState.Loading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            // Help text
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Column {
                        Text(
                            "Category Guidelines",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "• Use clear, descriptive names\n" +
                            "• Keep category hierarchy simple\n" +
                            "• Set display order to control sorting\n" +
                            "• Icon class uses Bootstrap Icons (bi-*)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
            
            // Extra spacing for FAB
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
