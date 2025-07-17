package com.example.lifetrack.ui.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lifetrack.data.repository.AuthRepositoryImpl
import com.example.lifetrack.data.repository.UserRepositoryImpl
import com.example.lifetrack.presenter.AuthPresenter
import com.example.lifetrack.ui.screens.AdminScreen
import com.example.lifetrack.ui.screens.HomeScreen
import com.example.lifetrack.ui.screens.LoginScreen
import com.example.lifetrack.ui.screens.RegistrationScreen
import com.example.lifetrack.ui.screens.SplashScreen
import com.example.lifetrack.ui.screens.RestoreScreen
import com.example.lifetrack.view.AuthView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(scope: CoroutineScope) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val userRepository = UserRepositoryImpl()
    val authRepository = AuthRepositoryImpl()
    val authPresenter = AuthPresenter(
        view = object : AuthView {
            override fun showLoading(isLoading: Boolean, message: String?) {
                coroutineScope.launch {
                    if (isLoading) {
                        val defaultMessage = "Loading, please wait..."
                        Toast.makeText(context, message ?: defaultMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun showError(message: String) {
                Toast.makeText(context, "Error: $message", Toast.LENGTH_LONG).show()
            }
            override fun onAuthSuccess() {
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            }
        },
        repository = authRepository,
        scope = scope
    )
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(navController)
        }
        composable("login") {
            LoginScreen(navController, authPresenter)
        }
        composable("home") {
            HomeScreen(
                navController = navController,
                onEmergency = { navController.navigate("emergency") },
                onSearch = { navController.navigate("search")},
                 onAlma = { navController.navigate("alma") }
            )
        }
        composable("signup") {
            RegistrationScreen(
                navController = navController,
                presenter = authPresenter
            )
        }
        composable("reset") {
            RestoreScreen(
                navController = navController,
                userRepository = userRepository
            )
        }
        composable ("admin"){
            AdminScreen(navController)
        }
    }
}