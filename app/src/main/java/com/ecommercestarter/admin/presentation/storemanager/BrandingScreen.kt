package com.ecommercestarter.admin.presentation.storemanager

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrandingScreen(
    onNavigateBack: () -> Unit,
    viewModel: BrandingViewModel = hiltViewModel()
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Site Identity", "Colors & Fonts", "Business Info", "Social Media")
    
    val brandingState by viewModel.brandingState.collectAsState()
    val operationState by viewModel.operationState.collectAsState()
    val formState by viewModel.formState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Handle operation result
    LaunchedEffect(operationState) {
        when (val state = operationState) {
            is BrandingOperationState.Success -> {
                snackbarHostState.showSnackbar(state.message)
                viewModel.resetOperationState()
            }
            is BrandingOperationState.Error -> {
                snackbarHostState.showSnackbar(state.message)
                viewModel.resetOperationState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Branding & Theme", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadBrandingSettings() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.saveBrandingSettings() },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Save, contentDescription = "Save")
            }
        }
    ) { paddingValues ->
        when (brandingState) {
            is BrandingState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is BrandingState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text((brandingState as BrandingState.Error).message)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadBrandingSettings() }) {
                            Text("Retry")
                        }
                    }
                }
            }
            is BrandingState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // Tab Row
                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = { Text(title, maxLines = 1) },
                                icon = {
                                    Icon(
                                        when (index) {
                                            0 -> Icons.Default.Business
                                            1 -> Icons.Default.Palette
                                            2 -> Icons.Default.Info
                                            else -> Icons.Default.Public
                                        },
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                    }

                    // Tab Content
                    when (selectedTabIndex) {
                        0 -> SiteIdentityTab(formState, viewModel, operationState)
                        1 -> ColorsAndFontsTab(formState, viewModel, operationState)
                        2 -> BusinessInfoTab(formState, viewModel, operationState)
                        3 -> SocialMediaTab(formState, viewModel, operationState)
                    }
                }
            }
        }
    }
}

@Composable
private fun SiteIdentityTab(
    formState: BrandingFormState,
    viewModel: BrandingViewModel,
    operationState: BrandingOperationState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Site Identity",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = formState.siteName,
            onValueChange = viewModel::updateSiteName,
            label = { Text("Site Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = formState.siteTagline,
            onValueChange = viewModel::updateSiteTagline,
            label = { Text("Site Tagline") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        OutlinedTextField(
            value = formState.companyName,
            onValueChange = viewModel::updateCompanyName,
            label = { Text("Company Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = formState.logoUrl,
            onValueChange = viewModel::updateLogoUrl,
            label = { Text("Logo URL") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("https://example.com/logo.png") }
        )
        
        OutlinedTextField(
            value = formState.faviconUrl,
            onValueChange = viewModel::updateFaviconUrl,
            label = { Text("Favicon URL") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("https://example.com/favicon.ico") }
        )
        
        OutlinedTextField(
            value = formState.heroImageUrl,
            onValueChange = viewModel::updateHeroImageUrl,
            label = { Text("Hero Image URL") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("https://example.com/hero.jpg") }
        )
        
        if (operationState is BrandingOperationState.Loading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        
        Spacer(modifier = Modifier.height(60.dp)) // Space for FAB
    }
}

@Composable
private fun ColorsAndFontsTab(
    formState: BrandingFormState,
    viewModel: BrandingViewModel,
    operationState: BrandingOperationState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Colors & Fonts",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = formState.primaryColor,
            onValueChange = viewModel::updatePrimaryColor,
            label = { Text("Primary Color") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("#007BFF") }
        )
        
        OutlinedTextField(
            value = formState.primaryDark,
            onValueChange = viewModel::updatePrimaryDark,
            label = { Text("Primary Dark") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("#0056B3") }
        )
        
        OutlinedTextField(
            value = formState.primaryLight,
            onValueChange = viewModel::updatePrimaryLight,
            label = { Text("Primary Light") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("#3395FF") }
        )

        OutlinedTextField(
            value = formState.secondaryColor,
            onValueChange = viewModel::updateSecondaryColor,
            label = { Text("Secondary Color") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("#6C757D") }
        )

        OutlinedTextField(
            value = formState.accentColor,
            onValueChange = viewModel::updateAccentColor,
            label = { Text("Accent Color") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("#28A745") }
        )
        
        OutlinedTextField(
            value = formState.primaryFont,
            onValueChange = viewModel::updatePrimaryFont,
            label = { Text("Primary Font") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("Arial, Helvetica, sans-serif") }
        )
        
        OutlinedTextField(
            value = formState.headingFont,
            onValueChange = viewModel::updateHeadingFont,
            label = { Text("Heading Font") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("Georgia, serif") }
        )
        
        if (operationState is BrandingOperationState.Loading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        
        Spacer(modifier = Modifier.height(60.dp))
    }
}

@Composable
private fun BusinessInfoTab(
    formState: BrandingFormState,
    viewModel: BrandingViewModel,
    operationState: BrandingOperationState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Business Information",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = formState.contactEmail,
            onValueChange = viewModel::updateContactEmail,
            label = { Text("Contact Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        OutlinedTextField(
            value = formState.supportEmail,
            onValueChange = viewModel::updateSupportEmail,
            label = { Text("Support Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = formState.address,
            onValueChange = viewModel::updateAddress,
            label = { Text("Business Address") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 4
        )
        
        OutlinedTextField(
            value = formState.city,
            onValueChange = viewModel::updateCity,
            label = { Text("City") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        OutlinedTextField(
            value = formState.state,
            onValueChange = viewModel::updateState,
            label = { Text("State/Province") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        OutlinedTextField(
            value = formState.country,
            onValueChange = viewModel::updateCountry,
            label = { Text("Country") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        if (operationState is BrandingOperationState.Loading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        
        Spacer(modifier = Modifier.height(60.dp))
    }
}

@Composable
private fun SocialMediaTab(
    formState: BrandingFormState,
    viewModel: BrandingViewModel,
    operationState: BrandingOperationState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Social Media",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = formState.facebookUrl,
            onValueChange = viewModel::updateFacebookUrl,
            label = { Text("Facebook URL") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("https://facebook.com/yourpage") }
        )
        
        OutlinedTextField(
            value = formState.twitterUrl,
            onValueChange = viewModel::updateTwitterUrl,
            label = { Text("Twitter/X URL") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("https://twitter.com/youraccount") }
        )
        
        OutlinedTextField(
            value = formState.instagramUrl,
            onValueChange = viewModel::updateInstagramUrl,
            label = { Text("Instagram URL") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("https://instagram.com/youraccount") }
        )
        
        OutlinedTextField(
            value = formState.linkedInUrl,
            onValueChange = viewModel::updateLinkedInUrl,
            label = { Text("LinkedIn URL") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("https://linkedin.com/company/yourcompany") }
        )
        
        if (operationState is BrandingOperationState.Loading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        
        Spacer(modifier = Modifier.height(60.dp))
    }
}
