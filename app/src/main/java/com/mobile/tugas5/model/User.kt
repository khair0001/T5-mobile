package com.mobile.tugas5.model

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val role: String,
    val is_active: Boolean
)
