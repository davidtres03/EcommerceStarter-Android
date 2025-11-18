package com.ecommercestarter.admin.domain.model

data class User(
    val id: String,
    val email: String,
    val name: String,
    val role: String,
    val avatarUrl: String?
)
