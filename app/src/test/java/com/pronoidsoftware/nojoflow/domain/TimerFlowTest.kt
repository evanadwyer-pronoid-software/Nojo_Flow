package com.pronoidsoftware.nojoflow.domain

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isLessThan
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.RepeatedTest
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.measureTime

class TimerFlowTest {

    @RepeatedTest(50)
    fun `timer finishes`() = runTest {
        val testTime = measureTime {
            timeAndEmitUntil(10f, 5.minutes).test {
                val first = awaitItem()
                assertThat(first).isEqualTo(ZERO)
                skipItems(3000)
                val last = awaitItem()
                assertThat(last).isLessThan(10.milliseconds)
            }
        }
        assertThat(testTime).isLessThan(500.milliseconds)
    }
}