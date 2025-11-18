package com.ecommercestarter.admin.data.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("token")
    val token: String?,
    
    @SerializedName("user")
    val user: UserDto?,
    
    @SerializedName("message")
    val message: String?
)
