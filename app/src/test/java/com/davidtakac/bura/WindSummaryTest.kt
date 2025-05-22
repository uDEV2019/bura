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

package com.davidtakac.bura

import com.davidtakac.bura.forecast.ForecastResult
import com.davidtakac.bura.gust.GustMoment
import com.davidtakac.bura.gust.GustPeriod
import com.davidtakac.bura.summary.wind.WindSummary
import com.davidtakac.bura.summary.wind.getWindSummary
import com.davidtakac.bura.wind.Wind
import com.davidtakac.bura.wind.WindDirection
import com.davidtakac.bura.wind.WindMoment
import com.davidtakac.bura.wind.WindPeriod
import com.davidtakac.bura.wind.WindSpeed
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.temporal.ChronoUnit

class WindSummaryTest {
    @Test
    fun `gets current wind speed, direction and gust speed`() = runTest {
        val time = unixEpochStart
        val now = time.plus(10, ChronoUnit.MINUTES)
        val windPeriod = WindPeriod(
            listOf(
                WindMoment(
                    time,
                    Wind(WindSpeed(0.0, WindSpeed.Unit.MetersPerSecond), WindDirection(0.0))
                )
            )
        )
        val gustPeriod = GustPeriod(listOf(
            GustMoment(
                time,
                WindSpeed(1.0, WindSpeed.Unit.MetersPerSecond)
            )))
        val summary = getWindSummary(now, windPeriod, gustPeriod)
        assertEquals(
            ForecastResult.Success(
                WindSummary(
                    windNow = Wind(
                        WindSpeed(0.0, WindSpeed.Unit.MetersPerSecond),
                        WindDirection(0.0)
                    ),
                    gustNow = WindSpeed(1.0, WindSpeed.Unit.MetersPerSecond)
                )
            ),
            summary
        )
    }

    @Test
    fun `outdated when no now`() = runTest {
        val time = unixEpochStart
        val now = time.plus(1, ChronoUnit.HOURS).plus(10, ChronoUnit.MINUTES)
        val windPeriod = WindPeriod(
            listOf(
                WindMoment(
                    time,
                    Wind(WindSpeed(0.0, WindSpeed.Unit.MetersPerSecond), WindDirection(0.0))
                )
            )
        )
        val gustPeriod = GustPeriod(listOf(
            GustMoment(
                time,
                WindSpeed(1.0, WindSpeed.Unit.MetersPerSecond)
            )))
        val summary = getWindSummary(now, windPeriod, gustPeriod)
        assertEquals(ForecastResult.Outdated, summary)
    }
}