package com.example.a212268_nazatulaini_lab1

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation(
    viewModel: ReServeViewModel,
    chatViewModel: ChatViewModel
) {
    val navController = rememberNavController()

    val goHome: () -> Unit = {
        navController.navigate("home") {
            popUpTo("home") { inclusive = false }
        }
    }

    fun navigateToDetail(itemName: String) {
        val userItem = viewModel.getUserListedItem(itemName)
        when {
            userItem != null -> {
                val encodedName = Uri.encode(userItem.name)
                val encodedCat  = Uri.encode(userItem.category)
                navController.navigate("my_listing_detail/$encodedName/$encodedCat")
            }
            viewModel.getFoodItems().any { it.name == itemName } ->
                navController.navigate("foodDetail/${Uri.encode(itemName)}")
            else ->
                navController.navigate("nonFoodDetail/${Uri.encode(itemName)}")
        }
    }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            ReServeApp(
                onFoodItemClick    = { navigateToDetail(it) },   // ← changed
                onNonFoodItemClick = { navigateToDetail(it) },   // ← changed
                onCartClick        = { navController.navigate("cart") },
                onAddClick         = { navController.navigate("add_item") },
                onEmailClick       = { owner, item -> navController.navigate("chat_detail/$owner/$item") },
                chatViewModel      = chatViewModel,
                onAllFoodClick     = { navController.navigate("category/Food") },
                onAllNonFoodClick  = { navController.navigate("category/Non-food") },
                onAllGoingSoonClick = { navController.navigate("going_soon") },
                viewModel          = viewModel
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
                onBack      = { navController.popBackStack() },
                onHomeClick = goHome,
                onViewItem  = { name, cat ->
                    // Route to the owner's own detail screen, not the buyer screen
                    navController.navigate("my_listing_detail/$name/$cat")
                },
                viewModel   = viewModel
            )
        }
        composable("going_soon") {
            GoingSoonScreen(
                onBack = { navController.popBackStack() },
                onItemClick = { navigateToDetail(it) },
                onHomeClick = goHome,
                viewModel = viewModel
            )
        }

        composable("my_listing_detail/{itemName}/{category}") { back ->
            val name = back.arguments?.getString("itemName") ?: ""
            val cat  = back.arguments?.getString("category") ?: "Food"
            MyListingDetailScreen(
                itemName    = name,
                category    = cat,
                onBack      = { navController.popBackStack() },
                onHomeClick = goHome,
                onDeleted   = {
                    // After deletion pop back to home so the deleted item
                    // is no longer reachable via the back stack.
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = false }
                    }
                },
                viewModel   = viewModel          // ← pass the shared instance
            )
        }
        composable("category/{filter}") { back ->
            val filter = back.arguments?.getString("filter") ?: "Food"
            CategoryScreen(
                filter             = filter,
                onBack             = { navController.popBackStack() },
                onHomeClick        = goHome,
                onFoodItemClick    = { navigateToDetail(it) },   // ← changed
                onNonFoodItemClick = { navigateToDetail(it) },   // ← changed
                viewModel          = viewModel
            )
        }
    }
}