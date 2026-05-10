package com.example.a212268_nazatulaini_lab1

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: ReServeViewModel = viewModel()
    val chatViewModel: ChatViewModel = viewModel()

    val goHome: () -> Unit = {
        navController.navigate("home") {
            popUpTo("home") { inclusive = false }
        }
    }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            ReServeApp(
                onFoodItemClick = { navController.navigate("foodDetail/$it") },
                onNonFoodItemClick = { navController.navigate("nonFoodDetail/$it") },
                onCartClick = { navController.navigate("cart") },
                onAddClick = { navController.navigate("add_item") },
                onEmailClick = { owner, item -> navController.navigate("chat_detail/$owner/$item") },
                chatViewModel = chatViewModel,
                onAllFoodClick = { navController.navigate("category/Food") },
                onAllNonFoodClick = { navController.navigate("category/Non-food") },
                onAllGoingSoonClick = { navController.navigate("going_soon") },
                viewModel = viewModel
            )
        }
        composable("foodDetail/{itemName}") { back ->
            val itemName = back.arguments?.getString("itemName") ?: ""
            FoodDetailScreen(
                itemName = itemName,
                onBack = { navController.popBackStack() },
                onHomeClick = goHome,
                onMessageOwner = { owner, item -> navController.navigate("chat_detail/$owner/$item") },
                viewModel = viewModel,
                chatViewModel = chatViewModel
            )
        }
        composable("nonFoodDetail/{itemName}") { back ->
            val itemName = back.arguments?.getString("itemName") ?: ""
            NonFoodDetailScreen(
                itemName = itemName,
                onBack = { navController.popBackStack() },
                onHomeClick = goHome,
                onMessageOwner = { owner, item -> navController.navigate("chat_detail/$owner/$item") },
                viewModel = viewModel,
                chatViewModel = chatViewModel
            )
        }
        composable("cart") {
            CartScreen(
                onBack = { navController.popBackStack() },
                onHomeClick = goHome,
                viewModel = viewModel
            )
        }
        composable("chat_detail/{ownerName}/{itemName}") { back ->
            val owner = back.arguments?.getString("ownerName") ?: ""
            val item = back.arguments?.getString("itemName") ?: ""
            ChatDetailScreen(
                ownerName = owner,
                itemName = item,
                onBack = { navController.popBackStack() },
                onHomeClick = goHome,
                chatViewModel = chatViewModel
            )
        }
        composable("add_item") {
            AddItemScreen(
                onBack = { navController.popBackStack() },
                onHomeClick = goHome,
                viewModel = viewModel
            )
        }
        composable("going_soon") {
            GoingSoonScreen(
                onBack = { navController.popBackStack() },
                onItemClick = { navController.navigate("foodDetail/$it") },
                onHomeClick = goHome,
                viewModel = viewModel
            )
        }
        composable("category/{filter}") { back ->
            val filter = back.arguments?.getString("filter") ?: "Food"
            CategoryScreen(
                filter = filter,
                onBack = { navController.popBackStack() },
                onHomeClick = goHome,
                onFoodItemClick = { navController.navigate("foodDetail/$it") },
                onNonFoodItemClick = { navController.navigate("nonFoodDetail/$it") },
                viewModel = viewModel
            )
        }
    }
}