package com.gmail.umidsafarov.lifebalance.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute

@Composable
fun Navigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.TasksListRoute,
    ) {
        composable<Routes.TasksListRoute> {
            TasksListDestination(
                route = it.toRoute(),
                onNavigateToTask = { id ->
                    navController.navigate(Routes.TaskRoute(id = id))
                },
                resultTaskId = navController.currentBackStackEntry?.savedStateHandle?.get(Routes.TasksListRoute.RESULT_TASK_ID),
                onResultTaskIdHandled = {
                    navController.currentBackStackEntry?.savedStateHandle?.remove<Long?>(Routes.TasksListRoute.RESULT_TASK_ID)
                },
            )
        }
        composable<Routes.TaskRoute> {
            TaskDestination(
                route = it.toRoute(),
                onBack = { resultId ->
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        Routes.TasksListRoute.RESULT_TASK_ID,
                        resultId
                    )
                    navController.popBackStack()
                },
            )
        }
    }
}