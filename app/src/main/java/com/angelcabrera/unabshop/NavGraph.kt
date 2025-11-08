

package com.angelcabrera.unabshop.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.angelcabrera.unabshop.AddProductScreen
import com.angelcabrera.unabshop.HomeScreen
import com.angelcabrera.unabshop.LoginScreen
import com.angelcabrera.unabshop.RegisterScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("home") { HomeScreen(navController = navController) }
        composable("addProduct") { AddProductScreen(navController = navController) }
    }
}
