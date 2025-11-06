package com.example.mathlab.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.domain.data.navigationRouting.MainMathObjRout
import com.example.domain.data.navigationRouting.NavRouteDecisionScreen
import com.example.mathlab.view.screens.DecisionScreen
import com.example.mathlab.view.screens.MathLabMainScreen


@Composable
internal fun MathNavHost(){
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = MainMathObjRout,
    ) {
        // 1. Анимация для MainMathObjRout
        composable<MainMathObjRout>(
            enterTransition = {
                // При входе на экран (A -> B), B скользит слева направо (Pop)
                // Обычный вход (первый запуск или переход, не связанный с Pop)
                fadeIn(animationSpec = tween(400)) // Используем только простой fadeIn
            },
            exitTransition = {
                // При выходе с экрана (A -> B), A скользит справа налево
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(400))
            },
            popEnterTransition = {
                // При возврате на этот экран (B -> A), A скользит слева направо
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(400))
            },
            popExitTransition = {
                // При возврате на этот экран (B -> A), B скользит справа налево (Pop)
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(400))
            }
        ) {
            MathLabMainScreen {category ->
                val navroute = NavRouteDecisionScreen(category = category)
                navController.navigate(navroute){
                    launchSingleTop = true
                }
            }
        }

        // 2. Анимация для NavRouteDecisionScreen
        composable<NavRouteDecisionScreen>(
            enterTransition = {
                // При входе на экран (A -> B), B скользит справа налево
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(400))
            },
            exitTransition = {
                // При выходе с экрана (A -> B), A скользит слева направо (Pop)
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(400))
            },
            popEnterTransition = {
                // При возврате на этот экран (B -> A), B скользит справа налево (Pop)
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(400))
            },
            popExitTransition = {
                // При возврате на этот экран (B -> A), A скользит справа налево
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(400))
            }
        ) {
            val category = it.toRoute<NavRouteDecisionScreen>()
            val hasBackStack = navController.previousBackStackEntry != null

            DecisionScreen(
                category = category.category,
                bacStack = hasBackStack,
                onBackIconTopAppBarClick = {
                    navController.popBackStack() // возврат на предыдущий экран
                }
            )
        }
    }

}