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

package com.davidtakac.bura.forecast

import com.davidtakac.bura.gust.GustMoment
import com.davidtakac.bura.gust.GustPeriod
import com.davidtakac.bura.humidity.HumidityMoment
import com.davidtakac.bura.humidity.HumidityPeriod
import com.davidtakac.bura.pop.PopMoment
import com.davidtakac.bura.pop.PopPeriod
import com.davidtakac.bura.precipitation.PrecipitationMoment
import com.davidtakac.bura.precipitation.PrecipitationPeriod
import com.davidtakac.bura.pressure.PressureMoment
import com.davidtakac.bura.pressure.PressurePeriod
import com.davidtakac.bura.sun.SunEvent
import com.davidtakac.bura.sun.SunMoment
import com.davidtakac.bura.sun.SunPeriod
import com.davidtakac.bura.temperature.TemperatureMoment
import com.davidtakac.bura.temperature.TemperaturePeriod
import com.davidtakac.bura.units.Units
import com.davidtakac.bura.uvindex.UvIndexMoment
import com.davidtakac.bura.uvindex.UvIndexPeriod
import com.davidtakac.bura.visibility.VisibilityMoment
import com.davidtakac.bura.visibility.VisibilityPeriod
import com.davidtakac.bura.condition.Condition
import com.davidtakac.bura.condition.ConditionMoment
import com.davidtakac.bura.condition.ConditionPeriod
import com.davidtakac.bura.precipitation.MixedPrecipitation
import com.davidtakac.bura.precipitation.Precipitation
import com.davidtakac.bura.wind.Wind
import com.davidtakac.bura.wind.WindMoment
import com.davidtakac.bura.wind.WindPeriod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ForecastConverter {
    suspend fun fromData(data: ForecastData, toUnits: Units): Forecast =
        withContext(Dispatchers.Default) {
            val temperatureMoments = mutableListOf<TemperatureMoment>()
            val feelsLikeMoments = mutableListOf<TemperatureMoment>()
            val dewPointMoments = mutableListOf<TemperatureMoment>()
            val popMoments = mutableListOf<PopMoment>()
            val precipMoments = mutableListOf<PrecipitationMoment>()
            val uvIndexMoments = mutableListOf<UvIndexMoment>()
            val windMoments = mutableListOf<WindMoment>()
            val gustMoments = mutableListOf<GustMoment>()
            val pressureMoments = mutableListOf<PressureMoment>()
            val visibilityMoments = mutableListOf<VisibilityMoment>()
            val humidityMoments = mutableListOf<HumidityMoment>()
            val conditionMoments = mutableListOf<ConditionMoment>()

            for (i in data.times.indices) {
                val time = data.times[i]
                temperatureMoments.add(TemperatureMoment(time, data.temperature[i].convertTo(toUnits.temperature)))
                feelsLikeMoments.add(TemperatureMoment(time, data.feelsLikeTemperature[i].convertTo(toUnits.temperature)))
                dewPointMoments.add(TemperatureMoment(time, data.dewPointTemperature[i].convertTo(toUnits.temperature)))
                popMoments.add(PopMoment(time, data.pop[i]))
                val rain = data.rain[i].convertTo(toUnits.rain)
                val showers = data.showers[i].convertTo(toUnits.showers)
                val snowfall = data.snow[i].convertTo(toUnits.snow)
                precipMoments.add(PrecipitationMoment(time, MixedPrecipitation(rain, showers, snowfall, Precipitation.Unit.Millimeters).convertTo(toUnits.precipitation)))
                uvIndexMoments.add(UvIndexMoment(time, data.uvIndex[i]))
                windMoments.add(WindMoment(time, Wind(data.windSpeed[i].convertTo(toUnits.windSpeed), data.windDirection[i])))
                gustMoments.add(GustMoment(time, data.gustSpeed[i].convertTo(toUnits.windSpeed)))
                pressureMoments.add(PressureMoment(time, data.pressure[i].convertTo(toUnits.pressure)))
                visibilityMoments.add(VisibilityMoment(time, data.visibility[i].convertTo(toUnits.visibility)))
                humidityMoments.add(HumidityMoment(time, data.humidity[i]))
                conditionMoments.add(ConditionMoment(time, Condition(data.wmoCode[i], data.isDay[i])))
            }

            val temperature = TemperaturePeriod(temperatureMoments)
            val feelsLike = TemperaturePeriod(feelsLikeMoments)
            val dewPoint = TemperaturePeriod(dewPointMoments)
            val pop = PopPeriod(popMoments)
            val precipitation = PrecipitationPeriod(precipMoments)
            val uvIndex = UvIndexPeriod(uvIndexMoments)
            val wind = WindPeriod(windMoments)
            val gust = GustPeriod(gustMoments)
            val pressure = PressurePeriod(pressureMoments)
            val visibility = VisibilityPeriod(visibilityMoments)
            val humidity = HumidityPeriod(humidityMoments)
            val weatherDescription = ConditionPeriod(conditionMoments)

            // McMurdo Station Pegasus Field returned the following on May 22:
            // sunrise":["2025-05-23T00:00","2025-05-24T00:00","2025-05-25T00:00","2025-05-26T00:00","2025-05-27T00:00","2025-05-28T00:00","2025-05-29T00:00"],"sunset":["2025-05-23T00:00","2025-05-24T00:00","2025-05-25T00:00","2025-05-26T00:00","2025-05-27T00:00","2025-05-28T00:00","2025-05-29T00:00"]}}
            // Sunrise and sunset at the exact same time does not make sense. This code removes such entries.
            val sortedSunMoments = mutableListOf<SunMoment>()
            for (i in data.sunrises.indices) {
                val sunrise = SunMoment(data.sunrises[i], SunEvent.Sunrise)
                val sunset = SunMoment(data.sunsets[i], SunEvent.Sunset)
                if (sunrise.time == sunset.time) {
                    continue
                } else if (sunset.time < sunrise.time) {
                    sortedSunMoments.add(sunset)
                    sortedSunMoments.add(sunrise)
                } else {
                    sortedSunMoments.add(sunrise)
                    sortedSunMoments.add(sunset)
                }
            }
            val sun = sortedSunMoments.takeIf { it.isNotEmpty() }?.let { SunPeriod(it) }

            return@withContext Forecast(
                temperature = temperature,
                feelsLike = feelsLike,
                dewPoint = dewPoint,
                sun = sun,
                pop = pop,
                precipitation = precipitation,
                uvIndex = uvIndex,
                wind = wind,
                gust = gust,
                pressure = pressure,
                visibility = visibility,
                humidity = humidity,
                condition = weatherDescription
            )
        }
}