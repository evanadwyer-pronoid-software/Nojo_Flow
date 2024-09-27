package com.pronoidsoftware.nojoflow.domain

import kotlinx.datetime.LocalDateTime

data class Note(
    val id: String,
    val title: String,
    val body: String,
    val createdAt: LocalDateTime,
    val lastUpdatedAt: LocalDateTime,
)
