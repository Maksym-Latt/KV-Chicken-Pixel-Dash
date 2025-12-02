package com.chicken.pixeldash.ui.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chicken.pixeldash.ui.screens.game.GameScreen
import com.chicken.pixeldash.ui.screens.menu.MenuScreen
import com.chicken.pixeldash.ui.screens.scores.ScoreScreen
import com.chicken.pixeldash.ui.screens.skins.SkinsScreen
import com.chicken.pixeldash.ui.screens.game.GameViewModel
import com.chicken.pixeldash.ui.screens.menu.MenuViewModel
import com.chicken.pixeldash.ui.screens.skins.SkinsViewModel

sealed class Screen(val route: String) {
    data object Menu : Screen("menu")
    data object Game : Screen("game")
    data object Skins : Screen("skins")
    data object Scores : Screen("scores")
}

@Composable
fun AppRoot(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Screen.Menu.route) {
        composable(Screen.Menu.route) {
            val vm: MenuViewModel = hiltViewModel()
            MenuScreen(
                viewModel = vm,
                onPlay = { navController.navigate(Screen.Game.route) },
                onSkins = { navController.navigate(Screen.Skins.route) },
            )
        }
        composable(Screen.Game.route) {
            val vm: GameViewModel = hiltViewModel()
            GameScreen(
                viewModel = vm,
                onExit = { navController.popBackStack(Screen.Menu.route, inclusive = false) }
            )
        }
        composable(Screen.Skins.route) {
            val vm: SkinsViewModel = hiltViewModel()
            SkinsScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Scores.route) {
            val vm: MenuViewModel = hiltViewModel()
            ScoreScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
