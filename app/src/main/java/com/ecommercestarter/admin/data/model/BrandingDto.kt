package com.ecommercestarter.admin.data.model

import com.google.gson.annotations.SerializedName

data class BrandingSettingsResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("data")
    val data: BrandingSettings
)

data class BrandingSettings(
    // Site Identity
    @SerializedName("siteName")
    val siteName: String = "",
    @SerializedName("siteTagline")
    val siteTagline: String? = null,
    @SerializedName("siteIcon")
    val siteIcon: String? = null,
    @SerializedName("logoUrl")
    val logoUrl: String? = null,
    @SerializedName("faviconUrl")
    val faviconUrl: String? = null,
    @SerializedName("heroImageUrl")
    val heroImageUrl: String? = null,
    
    // Colors & Fonts
    @SerializedName("primaryColor")
    val primaryColor: String = "#007BFF",
    @SerializedName("primaryDark")
    val primaryDark: String = "#0056B3",
    @SerializedName("primaryLight")
    val primaryLight: String = "#3395FF",
    @SerializedName("secondaryColor")
    val secondaryColor: String = "#6C757D",
    @SerializedName("accentColor")
    val accentColor: String = "#28A745",
    @SerializedName("primaryFont")
    val primaryFont: String = "Arial",
    @SerializedName("headingFont")
    val headingFont: String = "Arial",
    
    // Business Info
    @SerializedName("companyName")
    val companyName: String = "",
    @SerializedName("contactEmail")
    val contactEmail: String = "",
    @SerializedName("supportEmail")
    val supportEmail: String? = null,
    @SerializedName("address")
    val address: String? = null,
    @SerializedName("city")
    val city: String? = null,
    @SerializedName("state")
    val state: String? = null,
    @SerializedName("country")
    val country: String? = null,
    
    // Social Media
    @SerializedName("facebookUrl")
    val facebookUrl: String? = null,
    @SerializedName("twitterUrl")
    val twitterUrl: String? = null,
    @SerializedName("instagramUrl")
    val instagramUrl: String? = null,
    @SerializedName("linkedInUrl")
    val linkedInUrl: String? = null,
    
    // SEO
    @SerializedName("metaDescription")
    val metaDescription: String? = null,
    @SerializedName("metaKeywords")
    val metaKeywords: String? = null
)

data class UpdateBrandingRequest(
    // Site Identity
    @SerializedName("siteName")
    val siteName: String? = null,
    @SerializedName("siteTagline")
    val siteTagline: String? = null,
    @SerializedName("siteIcon")
    val siteIcon: String? = null,
    @SerializedName("logoUrl")
    val logoUrl: String? = null,
    @SerializedName("faviconUrl")
    val faviconUrl: String? = null,
    @SerializedName("heroImageUrl")
    val heroImageUrl: String? = null,
    
    // Colors & Fonts
    @SerializedName("primaryColor")
    val primaryColor: String? = null,
    @SerializedName("primaryDark")
    val primaryDark: String? = null,
    @SerializedName("primaryLight")
    val primaryLight: String? = null,
    @SerializedName("secondaryColor")
    val secondaryColor: String? = null,
    @SerializedName("accentColor")
    val accentColor: String? = null,
    @SerializedName("primaryFont")
    val primaryFont: String? = null,
    @SerializedName("headingFont")
    val headingFont: String? = null,
    
    // Business Info
    @SerializedName("companyName")
    val companyName: String? = null,
    @SerializedName("contactEmail")
    val contactEmail: String? = null,
    @SerializedName("supportEmail")
    val supportEmail: String? = null,
    @SerializedName("address")
    val address: String? = null,
    @SerializedName("city")
    val city: String? = null,
    @SerializedName("state")
    val state: String? = null,
    @SerializedName("country")
    val country: String? = null,
    
    // Social Media
    @SerializedName("facebookUrl")
    val facebookUrl: String? = null,
    @SerializedName("twitterUrl")
    val twitterUrl: String? = null,
    @SerializedName("instagramUrl")
    val instagramUrl: String? = null,
    @SerializedName("linkedInUrl")
    val linkedInUrl: String? = null
)

data class BrandingUpdateResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String
)
