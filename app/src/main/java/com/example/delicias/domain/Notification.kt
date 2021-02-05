package com.example.delicias.domain

data class Notification(
    val id: Long,
    val summary: String,
    val title: String,
    val date: String,
    val body: String
)
