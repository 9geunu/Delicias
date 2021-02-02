package com.example.delicias.domain

data class Notification(
    val id: Long,
    val notificationSummary: String,
    val date: String,
    val body: String
)
