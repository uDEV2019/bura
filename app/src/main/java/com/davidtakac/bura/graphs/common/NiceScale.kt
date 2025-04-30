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

package com.davidtakac.bura.graphs.common

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

// Source: Graphics Gems 1 by Andrew S. Glassner and https://stackoverflow.com/a/16363437
fun niceScale(min: Double, max: Double, maxTicks: Int): NiceScale {
    val range = niceNum(srcNum = max - min, round = false)
    val tickSpacing = niceNum(srcNum = range / (maxTicks - 1), round = true)
    val niceMin = floor(min / tickSpacing) * tickSpacing
    val niceMax = ceil(max / tickSpacing) * tickSpacing
    return NiceScale(
        min = niceMin,
        max = niceMax,
        spacing = tickSpacing
    )
}

private fun niceNum(srcNum: Double, round: Boolean): Double {
    val exponent: Double = floor(log10(srcNum))
    val fraction = srcNum / 10.0.pow(exponent)
    val niceFraction: Double = if (round) {
        when {
            fraction < 1.5 -> 1.0
            fraction < 3 -> 2.0
            fraction < 7.0 -> 5.0
            else -> 10.0
        }
    } else {
        when {
            fraction <= 1 -> 1.0
            fraction <= 2 -> 2.0
            fraction <= 5 -> 5.0
            else -> 10.0
        }
    }
    return niceFraction * 10.0.pow(exponent)
}

data class NiceScale(
    val min: Double,
    val max: Double,
    val spacing: Double
)
