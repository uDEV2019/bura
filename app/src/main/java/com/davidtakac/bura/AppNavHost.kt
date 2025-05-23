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

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.davidtakac.bura.common.Theme
import com.davidtakac.bura.graphs.EssentialGraphsDestination
import com.davidtakac.bura.settings.SettingsDestination
import com.davidtakac.bura.summary.SummaryDestination
import kotlinx.serialization.Serializable
import java.time.LocalDate

sealed interface AppRoutes {
    @Serializable
    data object Summary : AppRoutes

    @Serializable
    data class EssentialGraphs(val initialDay: String? = null) : AppRoutes

    @Serializable
    data object Settings : AppRoutes
}

@Composable
fun AppNavHost(theme: Theme, onThemeClick: (Theme) -> Unit) {
    val controller = rememberNavController()
    NavHost(navController = controller, startDestination = AppRoutes.Summary) {
        composable<AppRoutes.Summary> {
            SummaryDestination(
                onHourlySectionClick = {
                    controller.navigate(AppRoutes.EssentialGraphs())
                },
                onDayClick = {
                    controller.navigate(AppRoutes.EssentialGraphs(initialDay = it.toString()))
                },
                onSettingsButtonClick = {
                    controller.navigate(AppRoutes.Settings)
                },
                onPrecipitationClick = {
                    controller.navigate(AppRoutes.EssentialGraphs())
                }
            )
        }
        composable<AppRoutes.EssentialGraphs> { backStackEntry ->
            EssentialGraphsDestination(
                initialDay = backStackEntry.toRoute<AppRoutes.EssentialGraphs>().initialDay?.let(LocalDate::parse),
                onSelectPlaceClick = controller::navigateUp,
                onBackClick = controller::navigateUp
            )
        }
        composable<AppRoutes.Settings> {
            SettingsDestination(
                theme = theme,
                onThemeClick = onThemeClick,
                onBackClick = controller::navigateUp
            )
        }
    }
}