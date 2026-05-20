/*
 * Copyright 2024 David Takač
 *
 * This file is part of Bura.
 *
 * Bura is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Bura is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Bura. If not, see <https://www.gnu.org/licenses/>.
 */

package com.davidtakac.bura.summary.sun

import com.davidtakac.bura.forecast.ForecastResult
import com.davidtakac.bura.forecast.parameters.condition.Condition
import com.davidtakac.bura.forecast.parameters.condition.ConditionMoment
import com.davidtakac.bura.forecast.parameters.condition.ConditionPeriod
import com.davidtakac.bura.forecast.parameters.sun.SunEvent
import com.davidtakac.bura.forecast.parameters.sun.SunMoment
import com.davidtakac.bura.forecast.parameters.sun.SunPeriod
import com.davidtakac.bura.unixEpochStart
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import java.time.Duration
import java.time.temporal.ChronoUnit

class GetSunSummaryTest {
    @Test
    fun `sunrise and sunset soon`() = runTest {
        val now = unixEpochStart
        val firstMoment = now
        val secondMoment = now.plus(2, ChronoUnit.HOURS)
        val sunPeriod = SunPeriod(
            listOf(
                SunMoment(firstMoment, event = SunEvent.Rise),
                SunMoment(secondMoment, event = SunEvent.Set)
            )
        )
        val condPeriod = ConditionPeriod(
            listOf(
                ConditionMoment(firstMoment, condition = Condition(1, true))
            )
        )

        val summary = getSunSummary(now, sunPeriod, condPeriod)
        Assert.assertEquals(
            Sunrise.WithSunsetSoon(
                time = firstMoment.toLocalTime(),
                sunset = secondMoment.toLocalTime()
            ),
            (summary as ForecastResult.Success).data
        )
    }

    @Test
    fun `sunset and sunrise soon`() = runTest {
        val now = unixEpochStart
        val firstMoment = now
        val secondMoment = now.plus(2, ChronoUnit.HOURS)
        val sunPeriod = SunPeriod(
            listOf(
                SunMoment(firstMoment, event = SunEvent.Set),
                SunMoment(secondMoment, event = SunEvent.Rise)
            )
        )
        val condPeriod = ConditionPeriod(
            listOf(
                ConditionMoment(
                    firstMoment,
                    condition = Condition(1, false)
                )
            )
        )
        Assert.assertEquals(
            Sunset.WithSunriseSoon(
                time = firstMoment.toLocalTime(),
                sunrise = secondMoment.toLocalTime()
            ),
            (getSunSummary(now, sunPeriod, condPeriod) as ForecastResult.Success).data
        )
    }

    @Test
    fun `sunrise soon but sunset in two days`() = runTest {
        val now = unixEpochStart
        val firstMoment = now
        val secondMoment = now.plus(2, ChronoUnit.DAYS)
        val sunPeriod = SunPeriod(
            listOf(
                SunMoment(firstMoment, event = SunEvent.Rise),
                SunMoment(secondMoment, event = SunEvent.Set)
            )
        )

        val condPeriod = ConditionPeriod(
            listOf(
                ConditionMoment(firstMoment, condition = Condition(1, true))
            )
        )

        Assert.assertEquals(
            Sunrise.WithSunsetLater(
                time = firstMoment.toLocalTime(),
                sunset = secondMoment
            ),
            (getSunSummary(now, sunPeriod, condPeriod) as ForecastResult.Success).data
        )
    }

    @Test
    fun `sunset soon but sunrise in two days`() = runTest {
        val now = unixEpochStart
        val firstMoment = now
        val secondMoment = now.plus(2, ChronoUnit.DAYS)
        val sunPeriod = SunPeriod(
            listOf(
                SunMoment(firstMoment, event = SunEvent.Set),
                SunMoment(secondMoment, event = SunEvent.Rise)
            )
        )
        val condPeriod = ConditionPeriod(
            listOf(
                ConditionMoment(firstMoment, condition = Condition(1, true))
            )
        )
        Assert.assertEquals(
            Sunset.WithSunriseLater(
                time = firstMoment.toLocalTime(),
                sunrise = secondMoment
            ),
            (getSunSummary(now, sunPeriod, condPeriod) as ForecastResult.Success).data
        )
    }

    @Test
    fun `sunrise later`() = runTest {
        val now = unixEpochStart
        val firstMoment = now.plus(2, ChronoUnit.DAYS)
        val sunPeriod = SunPeriod(
            listOf(
                SunMoment(firstMoment, event = SunEvent.Rise),
            )
        )
        val condPeriod = ConditionPeriod(
            listOf(
                ConditionMoment(firstMoment, condition = Condition(1, true))
            )
        )
        Assert.assertEquals(
            Sunrise.Later(firstMoment),
            (getSunSummary(now, sunPeriod, condPeriod) as ForecastResult.Success).data
        )
    }

    @Test
    fun `sunset later`() = runTest {
        val now = unixEpochStart
        val firstMoment = now.plus(2, ChronoUnit.DAYS)
        val sunPeriod = SunPeriod(
            listOf(
                SunMoment(firstMoment, event = SunEvent.Set),
            )
        )
        val condPeriod = ConditionPeriod(
            listOf(
                ConditionMoment(firstMoment, condition = Condition(1, true))
            )
        )
        Assert.assertEquals(
            Sunset.Later(firstMoment),
            (getSunSummary(now, sunPeriod, condPeriod) as ForecastResult.Success).data
        )
    }

    @Test
    fun `night currently but no sunrise in sight`() = runTest {
        val now = unixEpochStart
        val sunPeriod = null
        val condPeriod = ConditionPeriod(List(48) {
            ConditionMoment(
                now.plus(it.toLong(), ChronoUnit.HOURS),
                condition = Condition(1, false)
            )
        })
        Assert.assertEquals(
            Sunrise.OutOfSight(Duration.ofHours(48)),
            (getSunSummary(now, sunPeriod, condPeriod) as ForecastResult.Success).data
        )
    }

    @Test
    fun `day currently but no sunset in sight`() = runTest {
        val now = unixEpochStart
        val sunPeriod = null
        val condPeriod = ConditionPeriod(List(48) {
            ConditionMoment(
                now.plus(it.toLong(), ChronoUnit.HOURS),
                condition = Condition(1, true)
            )
        })
        Assert.assertEquals(
            Sunset.OutOfSight(Duration.ofHours(48)),
            (getSunSummary(now, sunPeriod, condPeriod) as ForecastResult.Success).data
        )
    }

    @Test
    fun `when no current desc returns outdated`() = runTest {
        val start = unixEpochStart
        val sunPeriod = null
        val condPeriod = ConditionPeriod(List(48) {
            ConditionMoment(
                start.plus(it.toLong(), ChronoUnit.HOURS),
                condition = Condition(1, false)
            )
        })
        val now = start.plus(48.toLong(), ChronoUnit.HOURS)
        Assert.assertEquals(ForecastResult.Outdated, getSunSummary(now, sunPeriod, condPeriod))
    }
}