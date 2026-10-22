package com.example.louarayamacrame.ui.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.louarayamacrame.model.UserRole
import com.example.louarayamacrame.ui.screens.AdminCollectionsScreen
import com.example.louarayamacrame.ui.screens.AdminProductsScreen
import com.example.louarayamacrame.ui.screens.CatalogScreen
import com.example.louarayamacrame.ui.screens.CustomRequestScreen
import com.example.louarayamacrame.ui.screens.DashboardScreen
import com.example.louarayamacrame.ui.screens.FavoritesScreen
import com.example.louarayamacrame.ui.screens.LoginScreen
import com.example.louarayamacrame.ui.screens.MapScreen
import com.example.louarayamacrame.ui.screens.NotificationsScreen
import com.example.louarayamacrame.ui.screens.ProductDetailScreen
import com.example.louarayamacrame.ui.screens.ProfileScreen
import com.example.louarayamacrame.ui.screens.QrScannerScreen
import com.example.louarayamacrame.ui.screens.RegisterScreen
import com.example.louarayamacrame.ui.screens.RequestHistoryScreen
import com.example.louarayamacrame.ui.screens.WelcomeScreen
import com.example.louarayamacrame.ui.viewmodel.AdminViewModel
import com.example.louarayamacrame.ui.viewmodel.AuthViewModel
import com.example.louarayamacrame.ui.viewmodel.CatalogViewModel
import com.example.louarayamacrame.ui.viewmodel.DashboardViewModel
import com.example.louarayamacrame.ui.viewmodel.MapViewModel
import com.example.louarayamacrame.ui.viewmodel.NotificationViewModel
import com.example.louarayamacrame.ui.viewmodel.RequestViewModel

object ScreenRoutes {
    const val WELCOME = "welcome"
    const val REGISTER = "register"
    const val LOGIN = "login"
    const val DASHBOARD = "dashboard"
    const val MAP = "map"
    const val CATALOG = "catalog"
    const val PRODUCT_DETAIL = "product_detail/{productId}"
    const val QR_SCANNER = "qr_scanner"
    const val FAVORITES = "favorites"
    const val CUSTOM_REQUEST = "custom_request"
    const val ADMIN_PRODUCTS = "admin_products?productId={productId}"
    const val ADMIN_COLLECTIONS = "admin_collections"
    const val REQUEST_HISTORY = "request_history"
    const val NOTIFICATIONS = "notifications"
    const val PROFILE = "profile"

    fun productDetail(productId: String) = "product_detail/$productId"
    fun adminProducts(productId: String? = null) = if (productId != null) "admin_products?productId=$productId" else "admin_products"
}

@Composable
fun LouArayaNavGraph(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel(),
    dashboardViewModel: DashboardViewModel = viewModel(),
    mapViewModel: MapViewModel = viewModel(),
    catalogViewModel: CatalogViewModel = viewModel(),
    requestViewModel: RequestViewModel = viewModel(),
    notificationViewModel: NotificationViewModel = viewModel(),
    adminViewModel: AdminViewModel = viewModel()
) {
    val context = LocalContext.current
    val activeUser by authViewModel.activeUser.collectAsState()
    val isAdmin = activeUser?.role == UserRole.ADMIN

    fun showNotice(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

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

        // Pantalla 4: Principal / Dashboard
        composable(ScreenRoutes.DASHBOARD) {
            DashboardScreen(
                dashboardViewModel = dashboardViewModel,
                onNavigateToCatalog = { navController.navigate(ScreenRoutes.CATALOG) },
                onNavigateToProductDetail = { id -> navController.navigate(ScreenRoutes.productDetail(id)) },
                onNavigateToQrScanner = { navController.navigate(ScreenRoutes.QR_SCANNER) },
                onNavigateToFavorites = { navController.navigate(ScreenRoutes.FAVORITES) },
                onNavigateToCustomRequest = { navController.navigate(ScreenRoutes.CUSTOM_REQUEST) },
                onNavigateToAdminProducts = { navController.navigate(ScreenRoutes.adminProducts()) },
                onNavigateToAdminCollections = { navController.navigate(ScreenRoutes.ADMIN_COLLECTIONS) },
                onNavigateToRequestHistory = { navController.navigate(ScreenRoutes.REQUEST_HISTORY) },
                onNavigateToNotifications = { navController.navigate(ScreenRoutes.NOTIFICATIONS) },
                onNavigateToProfile = { navController.navigate(ScreenRoutes.PROFILE) },
                onNavigateToMap = { navController.navigate(ScreenRoutes.MAP) },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(ScreenRoutes.WELCOME) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Pantalla 5: Mapa de Ferias y Showrooms
        composable(ScreenRoutes.MAP) {
            MapScreen(
                mapViewModel = mapViewModel,
                isAdmin = isAdmin,
                onNavigateBack = { navController.popBackStack() },
                onNotice = ::showNotice
            )
        }

        // Pantalla 6: Catálogo de Diseños Artesanales
        composable(ScreenRoutes.CATALOG) {
            CatalogScreen(
                catalogViewModel = catalogViewModel,
                isAdmin = isAdmin,
                onNavigateBack = { navController.popBackStack() },
                onProductSelected = { id -> navController.navigate(ScreenRoutes.productDetail(id)) },
                onEditProduct = { id -> navController.navigate(ScreenRoutes.adminProducts(id)) },
                onAddProduct = { navController.navigate(ScreenRoutes.adminProducts()) }
            )
        }

        // Pantalla 7: Detalle del Producto
        composable(
            route = ScreenRoutes.PRODUCT_DETAIL,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            ProductDetailScreen(
                productId = productId,
                catalogViewModel = catalogViewModel,
                isAdmin = isAdmin,
                onNavigateBack = { navController.popBackStack() },
                onRequestCustomQuote = { navController.navigate(ScreenRoutes.CUSTOM_REQUEST) },
                onEditProduct = { id -> navController.navigate(ScreenRoutes.adminProducts(id)) }
            )
        }

        // Pantalla 8: Escáner QR de Producto
        composable(ScreenRoutes.QR_SCANNER) {
            QrScannerScreen(
                catalogViewModel = catalogViewModel,
                adminViewModel = adminViewModel,
                onNavigateBack = { navController.popBackStack() },
                onProductFound = { id -> navController.navigate(ScreenRoutes.productDetail(id)) }
            )
        }

        // Pantalla 9: Colección de Favoritos
        composable(ScreenRoutes.FAVORITES) {
            FavoritesScreen(
                catalogViewModel = catalogViewModel,
                onNavigateBack = { navController.popBackStack() },
                onProductSelected = { id -> navController.navigate(ScreenRoutes.productDetail(id)) },
                onRequestCustomQuote = { navController.navigate(ScreenRoutes.CUSTOM_REQUEST) }
            )
        }

        // Pantalla 10: Solicitud de Personalización y Cotización
        composable(ScreenRoutes.CUSTOM_REQUEST) {
            CustomRequestScreen(
                authViewModel = authViewModel,
                requestViewModel = requestViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHistory = {
                    navController.navigate(ScreenRoutes.REQUEST_HISTORY) {
                        popUpTo(ScreenRoutes.DASHBOARD)
                    }
                }
            )
        }

        // Pantalla 11: Gestión de Productos (Admin)
        composable(
            route = ScreenRoutes.ADMIN_PRODUCTS,
            arguments = listOf(navArgument("productId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            AdminProductsScreen(
                initialProductId = productId,
                catalogViewModel = catalogViewModel,
                adminViewModel = adminViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNotice = ::showNotice
            )
        }

        // Pantalla 12: Gestión de Colecciones y Campañas (Admin)
        composable(ScreenRoutes.ADMIN_COLLECTIONS) {
            AdminCollectionsScreen(
                adminViewModel = adminViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Pantalla 13: Historial y Gestión de Solicitudes
        composable(ScreenRoutes.REQUEST_HISTORY) {
            RequestHistoryScreen(
                authViewModel = authViewModel,
                requestViewModel = requestViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNewRequest = { navController.navigate(ScreenRoutes.CUSTOM_REQUEST) }
            )
        }

        // Pantalla 14: Centro de Notificaciones
        composable(ScreenRoutes.NOTIFICATIONS) {
            NotificationsScreen(
                notificationViewModel = notificationViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNotificationClick = { route ->
                    when (route) {
                        "catalog" -> navController.navigate(ScreenRoutes.CATALOG)
                        "map" -> navController.navigate(ScreenRoutes.MAP)
                        "request_history" -> navController.navigate(ScreenRoutes.REQUEST_HISTORY)
                        else -> navController.navigate(ScreenRoutes.DASHBOARD)
                    }
                }
            )
        }

        // Pantalla 15: Perfil y Configuración
        composable(ScreenRoutes.PROFILE) {
            ProfileScreen(
                authViewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(ScreenRoutes.WELCOME) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
