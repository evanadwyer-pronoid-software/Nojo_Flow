package com.pronoidsoftware.nojoflow.domain

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import kotlin.math.roundToLong
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

fun timeAndEmitUntil(
    emissionsPerSecond: Float,
    targetDuration: Duration,
): Flow<Duration> {
    return flow {
        var lastEmitTime = Clock.System.now().toEpochMilliseconds()
        var totalEmitTime = Duration.ZERO
        emit(totalEmitTime)
        while (totalEmitTime < targetDuration) {
            delay((1000L / emissionsPerSecond).roundToLong())
            val currentTime = Clock.System.now().toEpochMilliseconds()
            val elapsedTime = currentTime - lastEmitTime
            emit(elapsedTime.milliseconds)
            totalEmitTime += elapsedTime.milliseconds
            lastEmitTime = currentTime
        }
    }
}