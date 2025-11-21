package com.coperative.financialcardsApp.ui.navigation

import UserProfileScreen
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.coperative.financialcardsApp.ui.screens.CardDetailsScreen
import com.coperative.financialcardsApp.ui.screens.MyCardsScreen
import com.coperative.financialcardsApp.ui.screens.viewmodel.CardViewModel
import com.coperative.financialcardsApp.ui.screens.viewmodel.UserViewModel
sealed class NavRoutes(val route: String) {

    object MyCards : NavRoutes("my_cards")

    object CardDetails : NavRoutes("card_details/{cardId}") {
        fun passId(id: String) = "card_details/$id"
    }

    object Profile : NavRoutes("profile")
}

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.MyCards.route,
        modifier = modifier
            .padding(16.dp)
    ) {
        composable(NavRoutes.MyCards.route) {
            val viewModel: CardViewModel = hiltViewModel()
            MyCardsScreen(navController, viewModel)
        }
        composable(
            route = NavRoutes.CardDetails.route,
            arguments = listOf(navArgument("cardId") { type = NavType.StringType })
        ) { entry ->
            val cardId = entry.arguments?.getString("cardId") ?: ""

            val viewModel: CardViewModel = hiltViewModel()

            val card = viewModel.getCardById(cardId)

            if (card != null) {
                CardDetailsScreen(
                    card = card,
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }
        composable(NavRoutes.Profile.route) {
            val userViewModel: UserViewModel = hiltViewModel()
            UserProfileScreen(userViewModel)
        }
    }
}


