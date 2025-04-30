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
import kotlin.math.max
import kotlin.math.pow

fun looseLabel(min: Double, max: Double, ntick: Int): List<Double> {
    var nfrac: Int
    var d: Double
    var graphmin: Double
    var graphmax: Double
    var range: Double
    var x: Double

    range = niceNum(max - min, false)
    d = niceNum(range / (ntick - 1), true)
    graphmin = floor(min / d) * d
    graphmax = ceil(max / d) * d
    nfrac = max(-floor(log10(d)).toInt(), 0)

    // todo: pick up from here
    for (x in graphmin..graphmax + 0.5 * d)

    return listOf()
}

private fun niceNum(x: Double, round: Boolean): Double {
    val exp: Double = floor(log10(x))
    val f = x / 10.0.pow(exp)
    val nf: Double = if (round) {
        when {
            f < 1.5 -> 1.0
            f < 3 -> 2.0
            f < 7.0 -> 5.0
            else -> 10.0
        }
    } else {
        when {
            f <= 1 -> 1.0
            f <= 2 -> 2.0
            f <= 5 -> 5.0
            else -> 10.0
        }
    }
    return nf * 10.0.pow(exp)
}
