package com.example.louarayamacrame.ui.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.louarayamacrame.model.UserRole
import com.example.louarayamacrame.ui.screens.DashboardScreen
import com.example.louarayamacrame.ui.screens.LoginScreen
import com.example.louarayamacrame.ui.screens.MapScreen
import com.example.louarayamacrame.ui.screens.RegisterScreen
import com.example.louarayamacrame.ui.screens.WelcomeScreen
import com.example.louarayamacrame.ui.viewmodel.AuthViewModel
import com.example.louarayamacrame.ui.viewmodel.DashboardViewModel
import com.example.louarayamacrame.ui.viewmodel.MapViewModel

object ScreenRoutes {
    const val WELCOME = "welcome"
    const val REGISTER = "register"
    const val LOGIN = "login"
    const val DASHBOARD = "dashboard"
    const val MAP = "map"
}

@Composable
fun LouArayaNavGraph(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel(),
    dashboardViewModel: DashboardViewModel = viewModel(),
    mapViewModel: MapViewModel = viewModel()
) {
    val context = LocalContext.current
    val activeUser by authViewModel.activeUser.collectAsState()

    NavHost(
        navController = navController,
        startDestination = ScreenRoutes.WELCOME
    ) {
        // Pantalla 1: Bienvenida / Inicio
        composable(ScreenRoutes.WELCOME) {
            WelcomeScreen(
                onNavigateToLogin = { navController.navigate(ScreenRoutes.LOGIN) },
                onNavigateToRegister = { navController.navigate(ScreenRoutes.REGISTER) },
                onNavigateToMap = { navController.navigate(ScreenRoutes.MAP) }
            )
        }

        // Pantalla 2: Registro de Cliente
        composable(ScreenRoutes.REGISTER) {
            RegisterScreen(
                authViewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() },
                onRegisterSuccessToLogin = {
                    navController.navigate(ScreenRoutes.LOGIN) {
                        popUpTo(ScreenRoutes.WELCOME)
                    }
                }
            )
        }

        // Pantalla 3: Inicio de Sesión
        composable(ScreenRoutes.LOGIN) {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() },
                onLoginSuccess = {
                    navController.navigate(ScreenRoutes.DASHBOARD) {
                        popUpTo(ScreenRoutes.WELCOME) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(ScreenRoutes.REGISTER) }
            )
        }

        // Pantalla 4: Dashboard Principal (Cliente y Administrador)
        composable(ScreenRoutes.DASHBOARD) {
            DashboardScreen(
                dashboardViewModel = dashboardViewModel,
                onNavigateToMap = { navController.navigate(ScreenRoutes.MAP) },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(ScreenRoutes.WELCOME) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onQuickActionNotice = { message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            )
        }

        // Pantalla 5: Mapa de Ferias y Showrooms
        composable(ScreenRoutes.MAP) {
            val isAdmin = activeUser?.role == UserRole.ADMIN
            MapScreen(
                mapViewModel = mapViewModel,
                isAdmin = isAdmin,
                onNavigateBack = { navController.popBackStack() },
                onNotice = { message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}
