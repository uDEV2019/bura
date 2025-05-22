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

import com.davidtakac.bura.precipitation.MixedPrecipitation
import com.davidtakac.bura.precipitation.Precipitation
import com.davidtakac.bura.precipitation.PrecipitationMoment
import com.davidtakac.bura.precipitation.PrecipitationPeriod
import com.davidtakac.bura.precipitation.Rain
import com.davidtakac.bura.precipitation.Showers
import com.davidtakac.bura.precipitation.Snow
import org.junit.Assert.assertEquals
import org.junit.Test

class PrecipitationPeriodTest {
    @Test
    fun depth() {
        val period = PrecipitationPeriod(
            moments = listOf(
                PrecipitationMoment(
                    unixEpochStart,
                    MixedPrecipitation(
                        rain = Rain(1.0, Precipitation.Unit.Millimeters),
                        snow = Snow.Zero,
                        showers = Showers.Zero,
                        unit = Precipitation.Unit.Millimeters
                    ),
                )
            )
        )
        assertEquals(
            MixedPrecipitation(
                rain = Rain(1.0, Precipitation.Unit.Millimeters),
                snow = Snow.Zero,
                showers = Showers.Zero,
                unit = Precipitation.Unit.Millimeters
            ), period.total
        )
    }
}