package com.pronoidsoftware.nojoflow.presentation.ui

import java.util.Locale
import kotlin.time.Duration

fun Duration.format(): String =
    this.toComponents { minutes, seconds, _ ->
        String.format(
            Locale.getDefault(),
            "%d:%02d",
            minutes,
            seconds,
        )
    }