package com.pronoidsoftware.nojoflow.domain

import kotlinx.datetime.LocalDateTime
import java.util.UUID

data class Note(
    val id: UUID,
    val title: String,
    val body: String,
    val createdAt: LocalDateTime,
    val lastUpdatedAt: LocalDateTime,
)
