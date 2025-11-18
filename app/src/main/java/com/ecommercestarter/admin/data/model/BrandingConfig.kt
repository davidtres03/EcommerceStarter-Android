package com.ecommercestarter.admin.data.model

import com.google.gson.annotations.SerializedName

data class BrandingConfig(
    @SerializedName("businessName")
    val businessName: String,
    
    @SerializedName("logoUrl")
    val logoUrl: String,
    
    @SerializedName("primaryColor")
    val primaryColor: String,
    
    @SerializedName("secondaryColor")
    val secondaryColor: String,
    
    @SerializedName("accentColor")
    val accentColor: String,
    
    @SerializedName("backgroundColor")
    val backgroundColor: String,
    
    @SerializedName("surfaceColor")
    val surfaceColor: String,
    
    @SerializedName("textPrimaryColor")
    val textPrimaryColor: String,
    
    @SerializedName("textSecondaryColor")
    val textSecondaryColor: String,
    
    @SerializedName("faviconUrl")
    val faviconUrl: String,
    
    @SerializedName("supportEmail")
    val supportEmail: String,
    
    @SerializedName("supportPhone")
    val supportPhone: String
)
