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

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.LayoutDirection

fun DrawScope.drawVerticalAxis(
    steps: List<Double>,
    args: GraphArgs,
    measurer: TextMeasurer,
    stepFormatter: (step: Double) -> String?,
) {
    val lineX =
        if (layoutDirection == LayoutDirection.Ltr) size.width - args.endGutter
        else args.endGutter
    for (i in 0..steps.lastIndex) {
        val stepFraction = i.toDouble() / steps.lastIndex
        val plotBottom = size.height - args.bottomGutter
        val plotHeight = size.height - args.topGutter - args.bottomGutter
        val stepY = (plotBottom - plotHeight * stepFraction).toFloat()

        val horizontalLineStartX =
            if (layoutDirection == LayoutDirection.Ltr) args.startGutter
            else size.width - args.startGutter
        drawLine(
            color = args.axisColor,
            start = Offset(horizontalLineStartX, stepY),
            end = Offset(lineX, stepY)
        )

        val measuredText = measurer.measure(
            text = stepFormatter(steps[i]) ?: continue,
            style = args.axisTextStyle
        )
        val textTopLeftX =
            if (layoutDirection == LayoutDirection.Ltr) lineX + args.endAxisTextPaddingStart
            else lineX - args.endAxisTextPaddingStart - measuredText.size.width
        drawText(
            textLayoutResult = measuredText,
            color = args.axisColor,
            topLeft = Offset(
                x = textTopLeftX,
                y = stepY - (measuredText.size.height / 2)
            )
        )
    }
}

/**
 * Generates a list of vertical axis step values for graphing or charting purposes.
 *
 * This function calculates evenly spaced steps for a vertical axis based on a given
 * minimum and maximum value and a desired number of steps. The steps always begin
 * at or just below the [min] value, and padding is applied only at the top
 * (i.e., the final value may exceed [max]).
 *
 * - If [min] and [max] are equal, the steps are centered around that single value.
 * - If [min] and [max] differ, the function chooses a step size that covers the full
 *   range in [numSteps] steps, padding only at the upper end if needed.
 *
 * @param min The minimum value of the data range.
 * @param max The maximum value of the data range.
 * @param numSteps The number of desired intervals (steps) on the axis. Must be greater than 0.
 * @return A [VerticalAxisSteps] object containing all step values and the step size.
 *
 * @throws IllegalArgumentException if [numSteps] is not greater than 0 or [min] is greater than [max].
 */
fun generateVerticalAxisSteps(min: Double, max: Double, numSteps: Int): VerticalAxisSteps {
    require(numSteps > 0) { "Number of steps must be greater than 0" }
    require(min <= max) { "Lower bound must be less than or equal to upper bound" }

    val stepSize: Int
    val start: Int

    if (min == max) {
        stepSize = 1
        val center = min.toInt()
        val halfSteps = numSteps / 2
        start = center - halfSteps
    } else {
        val rawRange = max - min
        stepSize = kotlin.math.ceil(rawRange / numSteps).toInt()
        start = kotlin.math.floor(min).toInt()
    }

    return VerticalAxisSteps(
        all = List(numSteps + 1) { i -> (start + i * stepSize).toDouble() },
        stepSize = stepSize,
    )
}

/**
 * Data class representing the steps for a vertical axis.
 *
 * @property all The list of all step values (numSteps + 1 values to represent numSteps intervals).
 * @property stepSize The computed distance between each step.
 * @property first The first step value in the list.
 * @property last The last step value in the list.
 */
data class VerticalAxisSteps(
    val all: List<Double>,
    val stepSize: Int
) {
    val first: Double = all.first()
    val last: Double = all.last()
}