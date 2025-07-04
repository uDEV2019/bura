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

import com.davidtakac.bura.forecast.ForecastConverter
import com.davidtakac.bura.forecast.ForecastData
import com.davidtakac.bura.forecast.createSunPeriod
import com.davidtakac.bura.units.Units
import com.davidtakac.bura.humidity.Humidity
import com.davidtakac.bura.pop.Pop
import com.davidtakac.bura.precipitation.Precipitation
import com.davidtakac.bura.precipitation.Rain
import com.davidtakac.bura.precipitation.Showers
import com.davidtakac.bura.precipitation.Snow
import com.davidtakac.bura.pressure.Pressure
import com.davidtakac.bura.sun.SunEvent
import com.davidtakac.bura.sun.SunMoment
import com.davidtakac.bura.temperature.Temperature
import com.davidtakac.bura.uvindex.UvIndex
import com.davidtakac.bura.visibility.Visibility
import com.davidtakac.bura.wind.WindDirection
import com.davidtakac.bura.wind.WindSpeed
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import java.time.Instant
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class ForecastConverterTest {
    @Test
    fun `converts forecast data to imperial`() = runTest {
        val imperial = Units(
            temperature = Temperature.Unit.DegreesFahrenheit,
            rain = Precipitation.Unit.Inches,
            showers = Precipitation.Unit.Inches,
            snow = Precipitation.Unit.Inches,
            precipitation = Precipitation.Unit.Inches,
            windSpeed = WindSpeed.Unit.MilesPerHour,
            pressure = Pressure.Unit.InchesOfMercury,
            visibility = Visibility.Unit.Miles
        )
        val time = listOf(
            unixEpochStart,
            unixEpochStart.plus(1, ChronoUnit.HOURS)
        )
        val sunrises = listOf<LocalDateTime>()
        val sunsets = listOf<LocalDateTime>()
        val temperature = listOf(
            Temperature(0.0, Temperature.Unit.DegreesCelsius),
            Temperature(0.0, Temperature.Unit.DegreesCelsius)
        )
        val feelsLikeTemperature = listOf(
            Temperature(0.0, Temperature.Unit.DegreesCelsius),
            Temperature(0.0, Temperature.Unit.DegreesCelsius)
        )
        val dewPointTemperature = listOf(
            Temperature(0.0, Temperature.Unit.DegreesCelsius),
            Temperature(0.0, Temperature.Unit.DegreesCelsius)
        )
        val pop = listOf(
            Pop(value = 0.0),
            Pop(value = 0.0)
        )
        val isDay = listOf(true, false)
        val wmoCodes = listOf(1, 2)
        val rain = listOf(
            Rain(1.0, Precipitation.Unit.Millimeters),
            Rain(1.0, Precipitation.Unit.Millimeters)
        )
        val showers = listOf(
            Showers.ZeroMillimeters,
            Showers.ZeroMillimeters
        )
        val snowfall = listOf(
            Snow.ZeroMillimeters,
            Snow.ZeroMillimeters
        )
        val uvIndex = listOf(
            UvIndex(1),
            UvIndex(1)
        )
        val windSpeed = listOf(
            WindSpeed(1.0, WindSpeed.Unit.MetersPerSecond),
            WindSpeed(1.0, WindSpeed.Unit.MetersPerSecond)
        )
        val windDirection = listOf(
            WindDirection(10.0),
            WindDirection(10.0)
        )
        val gustSpeed = listOf(
            WindSpeed(10.0, WindSpeed.Unit.MetersPerSecond),
            WindSpeed(10.0, WindSpeed.Unit.MetersPerSecond)
        )
        val pressure = listOf(
            Pressure(1000.0, Pressure.Unit.Hectopascal),
            Pressure(1000.0, Pressure.Unit.Hectopascal)
        )
        val visibility = listOf(
            Visibility(1000.0, Visibility.Unit.Meters),
            Visibility(1000.0, Visibility.Unit.Meters)
        )
        val humidity = listOf(
            Humidity(80.0),
            Humidity(80.0)
        )
        val forecastData = ForecastData(
            timestamp = Instant.MIN,
            times = time,
            temperature = temperature,
            feelsLikeTemperature = feelsLikeTemperature,
            dewPointTemperature = dewPointTemperature,
            pop = pop,
            rain = rain,
            showers = showers,
            snow = snowfall,
            uvIndex = uvIndex,
            windSpeed = windSpeed,
            windDirection = windDirection,
            gustSpeed = gustSpeed,
            pressure = pressure,
            visibility = visibility,
            humidity = humidity,
            wmoCode = wmoCodes,
            isDay = isDay,
            sunrises = sunrises,
            sunsets = sunsets
        )
        val forecast = ForecastConverter().fromData(forecastData, toUnits = imperial)
        assertTrue(forecast.temperature.all { it.temperature.unit == imperial.temperature })
        assertTrue(forecast.feelsLike.all { it.temperature.unit == imperial.temperature })
        assertTrue(forecast.dewPoint.all { it.temperature.unit == imperial.temperature })
        assertTrue(forecast.precipitation.all { it.precipitation.unit == imperial.precipitation })
        assertTrue(forecast.wind.all { it.wind.speed.unit == imperial.windSpeed })
        assertTrue(forecast.gust.all { it.speed.unit == imperial.windSpeed })
        assertTrue(forecast.pressure.all { it.pressure.unit == imperial.pressure })
        assertTrue(forecast.visibility.all { it.visibility.unit == imperial.visibility })
    }

    @Test(expected = Exception::class)
    fun `throws when data does not match`() = runTest {
        val units = Units.Default
        val time = listOf(
            unixEpochStart,
            unixEpochStart.plus(1, ChronoUnit.HOURS)
        )
        val sunrises = listOf<LocalDateTime>()
        val sunsets = listOf<LocalDateTime>()
        val temperature = listOf(
            Temperature(0.0, Temperature.Unit.DegreesCelsius),
            Temperature(0.0, Temperature.Unit.DegreesCelsius)
        )
        val feelsLikeTemperature = listOf(
            Temperature(0.0, Temperature.Unit.DegreesCelsius),
            Temperature(0.0, Temperature.Unit.DegreesCelsius)
        )
        val dewPointTemperature = listOf(
            Temperature(0.0, Temperature.Unit.DegreesCelsius),
            Temperature(0.0, Temperature.Unit.DegreesCelsius)
        )
        val pop = listOf(
            Pop(value = 0.0),
            Pop(value = 0.0)
        )
        val isDay = listOf(true, false)
        val wmoCodes = listOf(1, 2)
        val rain = listOf(
            Rain(1.0, Precipitation.Unit.Millimeters),
            Rain(1.0, Precipitation.Unit.Millimeters)
        )
        val showers = listOf(
            Showers.ZeroMillimeters,
            Showers.ZeroMillimeters
        )
        val snowfall = listOf(
            Snow.ZeroMillimeters,
            Snow.ZeroMillimeters
        )
        val uvIndex = listOf(
            UvIndex(1),
            UvIndex(1)
        )
        val windSpeed = listOf(
            WindSpeed(1.0, WindSpeed.Unit.MetersPerSecond),
            WindSpeed(1.0, WindSpeed.Unit.MetersPerSecond)
        )
        val windDirection = listOf(
            WindDirection(10.0),
            WindDirection(10.0)
        )
        val gustSpeed = listOf(
            WindSpeed(10.0, WindSpeed.Unit.MetersPerSecond),
            WindSpeed(10.0, WindSpeed.Unit.MetersPerSecond)
        )
        val pressure = listOf(
            Pressure(1000.0, Pressure.Unit.Hectopascal),
            Pressure(1000.0, Pressure.Unit.Hectopascal)
        )
        val visibility = listOf(
            Visibility(1000.0, Visibility.Unit.Meters)
            // Mismatch, should throw
        )
        val humidity = listOf(
            Humidity(80.0),
            Humidity(80.0)
        )
        val forecastData = ForecastData(
            timestamp = Instant.MIN,
            times = time,
            temperature = temperature,
            feelsLikeTemperature = feelsLikeTemperature,
            dewPointTemperature = dewPointTemperature,
            pop = pop,
            rain = rain,
            showers = showers,
            snow = snowfall,
            uvIndex = uvIndex,
            windSpeed = windSpeed,
            windDirection = windDirection,
            gustSpeed = gustSpeed,
            pressure = pressure,
            visibility = visibility,
            humidity = humidity,
            wmoCode = wmoCodes,
            isDay = isDay,
            sunrises = sunrises,
            sunsets = sunsets
        )
        ForecastConverter().fromData(forecastData, units)
    }

    @Test
    fun `constructs sun period from regular data`() {
        val sunrises = listOf(LocalDateTime.parse("2025-01-01T08:00:00"), LocalDateTime.parse("2025-01-02T08:00:00"))
        val sunsets = listOf(LocalDateTime.parse("2025-01-01T20:00:00"), LocalDateTime.parse("2025-01-02T20:00:00"))
        val sunPeriod = createSunPeriod(sunrises, sunsets)
        assertEquals(
            listOf(
                SunMoment(LocalDateTime.parse("2025-01-01T08:00:00"), SunEvent.Sunrise),
                SunMoment(LocalDateTime.parse("2025-01-01T20:00:00"), SunEvent.Sunset),
                SunMoment(LocalDateTime.parse("2025-01-02T08:00:00"), SunEvent.Sunrise),
                SunMoment(LocalDateTime.parse("2025-01-02T20:00:00"), SunEvent.Sunset)
            ),
            sunPeriod?.moments
        )
    }

    @Test
    fun `handles all polar night`() {
        val sunrises = listOf(LocalDateTime.parse("2025-01-01T00:00:00"), LocalDateTime.parse("2025-01-02T00:00:00"))
        val sunsets = listOf(LocalDateTime.parse("2025-01-01T00:00:00"), LocalDateTime.parse("2025-01-02T00:00:00"))
        val sunPeriod = createSunPeriod(sunrises, sunsets)
        assertNull(sunPeriod)
    }

    @Test
    fun `handles all polar day`() {
        val sunrises = listOf(LocalDateTime.parse("2025-01-01T00:00:00"), LocalDateTime.parse("2025-01-02T00:00:00"))
        val sunsets = listOf(LocalDateTime.parse("2025-01-02T00:00:00"), LocalDateTime.parse("2025-01-03T00:00:00"))
        val sunPeriod = createSunPeriod(sunrises, sunsets)
        assertNull(sunPeriod)
    }

    @Test
    fun `handles polar night between regular data`() {
        val sunrises = listOf(LocalDateTime.parse("2025-01-01T08:00:00"), LocalDateTime.parse("2025-01-02T00:00:00"), LocalDateTime.parse("2025-01-03T08:00:00"))
        val sunsets = listOf(LocalDateTime.parse("2025-01-01T20:00:00"), LocalDateTime.parse("2025-01-02T00:00:00"), LocalDateTime.parse("2025-01-03T20:00:00"))
        val sunPeriod = createSunPeriod(sunrises, sunsets)
        assertEquals(
            listOf(
                SunMoment(LocalDateTime.parse("2025-01-01T08:00:00"), SunEvent.Sunrise),
                SunMoment(LocalDateTime.parse("2025-01-01T20:00:00"), SunEvent.Sunset),
                SunMoment(LocalDateTime.parse("2025-01-03T08:00:00"), SunEvent.Sunrise),
                SunMoment(LocalDateTime.parse("2025-01-03T20:00:00"), SunEvent.Sunset),
            ),
            sunPeriod?.moments
        )
    }

    @Test
    fun `handles polar day between regular data`() {
        val sunrises = listOf(LocalDateTime.parse("2025-01-01T08:00:00"), LocalDateTime.parse("2025-01-02T00:00:00"), LocalDateTime.parse("2025-01-03T08:00:00"))
        // I am not so sure that this is what OpenMeteo data would look if polar day is between regular data
        val sunsets = listOf(LocalDateTime.parse("2025-01-01T20:00:00"), LocalDateTime.parse("2025-01-03T00:00:00"), LocalDateTime.parse("2025-01-03T20:00:00"))
        val sunPeriod = createSunPeriod(sunrises, sunsets)
        assertEquals(
            listOf(
                SunMoment(LocalDateTime.parse("2025-01-01T08:00:00"), SunEvent.Sunrise),
                SunMoment(LocalDateTime.parse("2025-01-01T20:00:00"), SunEvent.Sunset),
                SunMoment(LocalDateTime.parse("2025-01-03T08:00:00"), SunEvent.Sunrise),
                SunMoment(LocalDateTime.parse("2025-01-03T20:00:00"), SunEvent.Sunset),
            ),
            sunPeriod?.moments
        )
    }
}