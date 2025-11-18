package com.ecommercestarter.admin.data.model

import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("role")
    val role: String,
    
    @SerializedName("avatarUrl")
    val avatarUrl: String?
)
