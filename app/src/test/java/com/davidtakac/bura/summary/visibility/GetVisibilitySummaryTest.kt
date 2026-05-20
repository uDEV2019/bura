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

package com.davidtakac.bura.summary.visibility

import com.davidtakac.bura.forecast.ForecastResult
import com.davidtakac.bura.forecast.parameters.visibility.Visibility
import com.davidtakac.bura.forecast.parameters.visibility.VisibilityMoment
import com.davidtakac.bura.forecast.parameters.visibility.VisibilityPeriod
import com.davidtakac.bura.unixEpochStart
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import java.time.temporal.ChronoUnit

class GetVisibilitySummaryTest {
    private val period = VisibilityPeriod(
        listOf(
            VisibilityMoment(unixEpochStart, Visibility(1.0, Visibility.Unit.Meters)),
            VisibilityMoment(
                unixEpochStart.plus(1, ChronoUnit.HOURS),
                Visibility(2.0, Visibility.Unit.Meters)
            ),
            VisibilityMoment(
                unixEpochStart.plus(2, ChronoUnit.HOURS),
                Visibility(3.0, Visibility.Unit.Meters)
            )
        )
    )

    @Test
    fun `gets distance and description of now`() = runTest {
        val now = unixEpochStart.plus(1, ChronoUnit.HOURS).plus(10, ChronoUnit.MINUTES)
        Assert.assertEquals(
            Visibility(2.0, Visibility.Unit.Meters),
            (getVisibilitySummary(now, period) as ForecastResult.Success).data.now
        )
    }

    @Test
    fun `summary is outdated when no now`() = runTest {
        val now = unixEpochStart.plus(3, ChronoUnit.HOURS).plus(10, ChronoUnit.MINUTES)
        Assert.assertEquals(ForecastResult.Outdated, getVisibilitySummary(now, period))
    }
}