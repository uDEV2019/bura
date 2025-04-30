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

fun generateVerticalAxisSteps(
    min: Double,
    max: Double,
    steps: Int,
    paddingLocation: VerticalAxisPaddingStrategy,
): VerticalAxisSteps {
    require(steps >= 2)
    require(min <= max)

    val stepSize: Double
    val start: Double
    if (min == max) {
        stepSize = 1.0
        start = when (paddingLocation) {
            VerticalAxisPaddingStrategy.Top -> min
            VerticalAxisPaddingStrategy.TopAndBottom -> min - steps / 2.0
        }
    } else {
        val rawRange = max - min
        stepSize = kotlin.math.ceil(rawRange / steps)
        val paddedRange = stepSize * steps
        val midpoint = (min + max) / 2
        start = when (paddingLocation) {
            VerticalAxisPaddingStrategy.Top -> TODO()
            VerticalAxisPaddingStrategy.TopAndBottom -> kotlin.math.floor(midpoint - paddedRange / 2)
        }
    }

    return VerticalAxisSteps(
        all = List(steps) { i -> start + i * stepSize },
        stepSize = stepSize,
    )
}

enum class VerticalAxisPaddingStrategy {
    Top, TopAndBottom
}

data class VerticalAxisSteps(
    val all: List<Double>,
    val stepSize: Double
) {
    val first: Double = all.first()
    val last: Double = all.last()
}