package com.ecommercestarter.admin.presentation.storemanager

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecommercestarter.admin.data.model.BrandingSettings
import com.ecommercestarter.admin.data.model.UpdateBrandingRequest
import com.ecommercestarter.admin.data.repository.BrandingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BrandingViewModel @Inject constructor(
    private val brandingRepository: BrandingRepository
) : ViewModel() {
    
    private val _brandingState = MutableStateFlow<BrandingState>(BrandingState.Loading)
    val brandingState: StateFlow<BrandingState> = _brandingState.asStateFlow()
    
    private val _operationState = MutableStateFlow<BrandingOperationState>(BrandingOperationState.Idle)
    val operationState: StateFlow<BrandingOperationState> = _operationState.asStateFlow()
    
    // Form state for all tabs
    private val _formState = MutableStateFlow(BrandingFormState())
    val formState: StateFlow<BrandingFormState> = _formState.asStateFlow()
    
    init {
        loadBrandingSettings()
    }
    
    fun loadBrandingSettings() {
        viewModelScope.launch {
            _brandingState.value = BrandingState.Loading
            brandingRepository.getBrandingSettings().fold(
                onSuccess = { settings ->
                    _brandingState.value = BrandingState.Success(settings)
                    // Populate form state
                    _formState.value = BrandingFormState(
                        siteName = settings.siteName,
                        siteTagline = settings.siteTagline ?: "",
                        logoUrl = settings.logoUrl ?: "",
                        faviconUrl = settings.faviconUrl ?: "",
                        heroImageUrl = settings.heroImageUrl ?: "",
                        siteIcon = settings.siteIcon ?: "",
                        primaryColor = settings.primaryColor,
                        primaryDark = settings.primaryDark,
                        primaryLight = settings.primaryLight,
                        secondaryColor = settings.secondaryColor,
                        accentColor = settings.accentColor,
                        primaryFont = settings.primaryFont,
                        headingFont = settings.headingFont,
                        companyName = settings.companyName,
                        contactEmail = settings.contactEmail,
                        supportEmail = settings.supportEmail ?: "",
                        address = settings.address ?: "",
                        city = settings.city ?: "",
                        state = settings.state ?: "",
                        country = settings.country ?: "",
                        facebookUrl = settings.facebookUrl ?: "",
                        twitterUrl = settings.twitterUrl ?: "",
                        instagramUrl = settings.instagramUrl ?: "",
                        linkedInUrl = settings.linkedInUrl ?: "",
                        metaDescription = settings.metaDescription ?: "",
                        metaKeywords = settings.metaKeywords ?: ""
                    )
                },
                onFailure = { error ->
                    _brandingState.value = BrandingState.Error(error.message ?: "Failed to load branding settings")
                }
            )
        }
    }
    
    fun saveBrandingSettings() {
        viewModelScope.launch {
            _operationState.value = BrandingOperationState.Loading
            
            val currentForm = _formState.value
            val request = UpdateBrandingRequest(
                siteName = currentForm.siteName.takeIf { it.isNotBlank() },
                siteTagline = currentForm.siteTagline.takeIf { it.isNotBlank() },
                logoUrl = currentForm.logoUrl.takeIf { it.isNotBlank() },
                faviconUrl = currentForm.faviconUrl.takeIf { it.isNotBlank() },
                heroImageUrl = currentForm.heroImageUrl.takeIf { it.isNotBlank() },
                siteIcon = currentForm.siteIcon.takeIf { it.isNotBlank() },
                primaryColor = currentForm.primaryColor.takeIf { it.isNotBlank() },
                primaryDark = currentForm.primaryDark.takeIf { it.isNotBlank() },
                primaryLight = currentForm.primaryLight.takeIf { it.isNotBlank() },
                secondaryColor = currentForm.secondaryColor.takeIf { it.isNotBlank() },
                accentColor = currentForm.accentColor.takeIf { it.isNotBlank() },
                primaryFont = currentForm.primaryFont.takeIf { it.isNotBlank() },
                headingFont = currentForm.headingFont.takeIf { it.isNotBlank() },
                companyName = currentForm.companyName.takeIf { it.isNotBlank() },
                contactEmail = currentForm.contactEmail.takeIf { it.isNotBlank() },
                supportEmail = currentForm.supportEmail.takeIf { it.isNotBlank() },
                address = currentForm.address.takeIf { it.isNotBlank() },
                city = currentForm.city.takeIf { it.isNotBlank() },
                state = currentForm.state.takeIf { it.isNotBlank() },
                country = currentForm.country.takeIf { it.isNotBlank() },
                facebookUrl = currentForm.facebookUrl.takeIf { it.isNotBlank() },
                twitterUrl = currentForm.twitterUrl.takeIf { it.isNotBlank() },
                instagramUrl = currentForm.instagramUrl.takeIf { it.isNotBlank() },
                linkedInUrl = currentForm.linkedInUrl.takeIf { it.isNotBlank() }
            )
            
            Log.d("BrandingViewModel", "Saving branding settings...")
            brandingRepository.updateBrandingSettings(request).fold(
                onSuccess = { message ->
                    _operationState.value = BrandingOperationState.Success(message)
                    Log.d("BrandingViewModel", "Successfully saved branding settings")
                    // Reload to get fresh data
                    loadBrandingSettings()
                },
                onFailure = { error ->
                    val errorMessage = error.message ?: "Failed to save branding settings"
                    _operationState.value = BrandingOperationState.Error(errorMessage)
                    Log.e("BrandingViewModel", "Failed to save branding settings: $errorMessage")
                }
            )
        }
    }
    
    // Form field update methods
    fun updateSiteName(value: String) {
        _formState.value = _formState.value.copy(siteName = value)
    }
    
    fun updateSiteTagline(value: String) {
        _formState.value = _formState.value.copy(siteTagline = value)
    }
    
    fun updateLogoUrl(value: String) {
        _formState.value = _formState.value.copy(logoUrl = value)
    }
    
    fun updateFaviconUrl(value: String) {
        _formState.value = _formState.value.copy(faviconUrl = value)
    }
    
    fun updateHeroImageUrl(value: String) {
        _formState.value = _formState.value.copy(heroImageUrl = value)
    }
    
    fun updateSiteIcon(value: String) {
        _formState.value = _formState.value.copy(siteIcon = value)
    }
    
    fun updatePrimaryColor(value: String) {
        _formState.value = _formState.value.copy(primaryColor = value)
    }
    
    fun updatePrimaryDark(value: String) {
        _formState.value = _formState.value.copy(primaryDark = value)
    }
    
    fun updatePrimaryLight(value: String) {
        _formState.value = _formState.value.copy(primaryLight = value)
    }
    
    fun updateSecondaryColor(value: String) {
        _formState.value = _formState.value.copy(secondaryColor = value)
    }
    
    fun updateAccentColor(value: String) {
        _formState.value = _formState.value.copy(accentColor = value)
    }
    
    fun updatePrimaryFont(value: String) {
        _formState.value = _formState.value.copy(primaryFont = value)
    }
    
    fun updateHeadingFont(value: String) {
        _formState.value = _formState.value.copy(headingFont = value)
    }
    
    fun updateCompanyName(value: String) {
        _formState.value = _formState.value.copy(companyName = value)
    }
    
    fun updateContactEmail(value: String) {
        _formState.value = _formState.value.copy(contactEmail = value)
    }
    
    fun updateSupportEmail(value: String) {
        _formState.value = _formState.value.copy(supportEmail = value)
    }
    
    fun updateAddress(value: String) {
        _formState.value = _formState.value.copy(address = value)
    }
    
    fun updateCity(value: String) {
        _formState.value = _formState.value.copy(city = value)
    }
    
    fun updateState(value: String) {
        _formState.value = _formState.value.copy(state = value)
    }
    
    fun updateCountry(value: String) {
        _formState.value = _formState.value.copy(country = value)
    }
    
    fun updateFacebookUrl(value: String) {
        _formState.value = _formState.value.copy(facebookUrl = value)
    }
    
    fun updateTwitterUrl(value: String) {
        _formState.value = _formState.value.copy(twitterUrl = value)
    }
    
    fun updateInstagramUrl(value: String) {
        _formState.value = _formState.value.copy(instagramUrl = value)
    }
    
    fun updateLinkedInUrl(value: String) {
        _formState.value = _formState.value.copy(linkedInUrl = value)
    }
    
    fun updateMetaDescription(value: String) {
        _formState.value = _formState.value.copy(metaDescription = value)
    }
    
    fun updateMetaKeywords(value: String) {
        _formState.value = _formState.value.copy(metaKeywords = value)
    }
    
    fun resetOperationState() {
        _operationState.value = BrandingOperationState.Idle
    }
}

sealed class BrandingState {
    object Loading : BrandingState()
    data class Success(val settings: BrandingSettings) : BrandingState()
    data class Error(val message: String) : BrandingState()
}

data class BrandingFormState(
    // Site Identity
    val siteName: String = "",
    val siteTagline: String = "",
    val logoUrl: String = "",
    val faviconUrl: String = "",
    val heroImageUrl: String = "",
    val siteIcon: String = "",
    
    // Colors & Fonts
    val primaryColor: String = "#007BFF",
    val primaryDark: String = "#0056B3",
    val primaryLight: String = "#3395FF",
    val secondaryColor: String = "#6C757D",
    val accentColor: String = "#28A745",
    val primaryFont: String = "Arial",
    val headingFont: String = "Arial",
    
    // Business Info
    val companyName: String = "",
    val contactEmail: String = "",
    val supportEmail: String = "",
    val address: String = "",
    val city: String = "",
    val state: String = "",
    val country: String = "",
    
    // Social Media
    val facebookUrl: String = "",
    val twitterUrl: String = "",
    val instagramUrl: String = "",
    val linkedInUrl: String = "",
    
    // SEO
    val metaDescription: String = "",
    val metaKeywords: String = ""
)

sealed class BrandingOperationState {
    object Idle : BrandingOperationState()
    object Loading : BrandingOperationState()
    data class Success(val message: String) : BrandingOperationState()
    data class Error(val message: String) : BrandingOperationState()
}
