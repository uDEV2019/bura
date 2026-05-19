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

import com.davidtakac.bura.forecast.parameters.precipitation.MixedPrecipitation
import com.davidtakac.bura.forecast.parameters.precipitation.Precipitation
import com.davidtakac.bura.forecast.parameters.precipitation.PrecipitationMoment
import com.davidtakac.bura.forecast.parameters.precipitation.PrecipitationPeriod
import com.davidtakac.bura.forecast.parameters.precipitation.Rain
import com.davidtakac.bura.forecast.parameters.precipitation.Showers
import com.davidtakac.bura.forecast.parameters.precipitation.Snow
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.temporal.ChronoUnit

class PrecipitationPeriodTest {
    @Test
    fun depth() {
        val period = PrecipitationPeriod(
            moments = listOf(
                PrecipitationMoment(
                    unixEpochStart,
                    MixedPrecipitation(
                        rain = Rain(1.0, Precipitation.Unit.Millimeters),
                        snow = Snow.ZeroMillimeters,
                        showers = Showers.ZeroMillimeters,
                        unit = Precipitation.Unit.Millimeters
                    ),
                )
            )
        )
        assertEquals(
            MixedPrecipitation(
                rain = Rain(1.0, Precipitation.Unit.Millimeters),
                snow = Snow.ZeroMillimeters,
                showers = Showers.ZeroMillimeters,
                unit = Precipitation.Unit.Millimeters
            ), period.total
        )
    }

    @Test
    fun convert() {
        val period = PrecipitationPeriod(
            moments = listOf(
                PrecipitationMoment(
                    hour = unixEpochStart,
                    precipitation = MixedPrecipitation(
                        rain = Rain(1.0, Precipitation.Unit.Millimeters),
                        snow = Snow.ZeroMillimeters,
                        showers = Showers.ZeroMillimeters,
                        unit = Precipitation.Unit.Millimeters
                    )
                ),
                PrecipitationMoment(
                    hour = unixEpochStart.plus(1, ChronoUnit.HOURS),
                    precipitation = MixedPrecipitation(
                        rain = Rain(1.0, Precipitation.Unit.Millimeters),
                        snow = Snow.ZeroMillimeters,
                        showers = Showers.ZeroMillimeters,
                        unit = Precipitation.Unit.Millimeters
                    )
                )
            )
        )
        val converted = period.convertTo(Precipitation.Unit.Inches)
        assert(converted.all { it.precipitation.unit == Precipitation.Unit.Inches })
    }
}