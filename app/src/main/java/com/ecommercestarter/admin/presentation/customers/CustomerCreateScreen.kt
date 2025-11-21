package com.ecommercestarter.admin.presentation.customers

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerCreateScreen(
    onNavigateBack: () -> Unit,
    onCustomerCreated: () -> Unit,
    viewModel: CustomerCreateViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val firstName by viewModel.firstName.collectAsState()
    val lastName by viewModel.lastName.collectAsState()
    val email by viewModel.email.collectAsState()
    val phone by viewModel.phone.collectAsState()
    val address by viewModel.address.collectAsState()
    val city by viewModel.city.collectAsState()
    val state by viewModel.state.collectAsState()
    val zipCode by viewModel.zipCode.collectAsState()
    val country by viewModel.country.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // Handle success state
    LaunchedEffect(uiState) {
        when (val currentState = uiState) {
            is CustomerCreateUiState.Success -> {
                snackbarHostState.showSnackbar("Customer created successfully!")
                onCustomerCreated()
            }
            is CustomerCreateUiState.Error -> {
                snackbarHostState.showSnackbar(currentState.message)
                viewModel.clearError()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Customer") },
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
            ExtendedFloatingActionButton(
                onClick = { viewModel.createCustomer() },
                text = { Text("Create Customer") },
                icon = { },
                expanded = true
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // First Name
            OutlinedTextField(
                value = firstName,
                onValueChange = { viewModel.onFirstNameChange(it) },
                label = { Text("First Name *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = uiState !is CustomerCreateUiState.Loading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Last Name
            OutlinedTextField(
                value = lastName,
                onValueChange = { viewModel.onLastNameChange(it) },
                label = { Text("Last Name *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = uiState !is CustomerCreateUiState.Loading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email
            OutlinedTextField(
                value = email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = { Text("Email *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                enabled = uiState !is CustomerCreateUiState.Loading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Phone
            OutlinedTextField(
                value = phone,
                onValueChange = { viewModel.onPhoneChange(it) },
                label = { Text("Phone") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                enabled = uiState !is CustomerCreateUiState.Loading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Address
            OutlinedTextField(
                value = address,
                onValueChange = { viewModel.onAddressChange(it) },
                label = { Text("Address") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = uiState !is CustomerCreateUiState.Loading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // City
            OutlinedTextField(
                value = city,
                onValueChange = { viewModel.onCityChange(it) },
                label = { Text("City") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = uiState !is CustomerCreateUiState.Loading
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // State
                OutlinedTextField(
                    value = state,
                    onValueChange = { viewModel.onStateChange(it) },
                    label = { Text("State") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    enabled = uiState !is CustomerCreateUiState.Loading
                )

                // Zip Code
                OutlinedTextField(
                    value = zipCode,
                    onValueChange = { viewModel.onZipCodeChange(it) },
                    label = { Text("Zip Code") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    enabled = uiState !is CustomerCreateUiState.Loading
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Country
            OutlinedTextField(
                value = country,
                onValueChange = { viewModel.onCountryChange(it) },
                label = { Text("Country") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = uiState !is CustomerCreateUiState.Loading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Loading indicator
            if (uiState is CustomerCreateUiState.Loading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            Spacer(modifier = Modifier.height(80.dp)) // Space for FAB
        }
    }
}
