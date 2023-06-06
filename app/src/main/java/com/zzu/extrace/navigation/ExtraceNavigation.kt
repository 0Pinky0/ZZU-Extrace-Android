package com.zzu.extrace.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.zzu.extrace.ui.screen.express.ShowExpressScreen
import com.zzu.extrace.ui.screen.express.CreateExpressScreen
import com.zzu.extrace.ui.screen.home.HomeScreen
import com.zzu.extrace.ui.screen.login.LoginScreen
import com.zzu.extrace.ui.screen.login.RegisterScreen
import com.zzu.extrace.ui.screen.map.MapScreen
import com.zzu.extrace.ui.screen.packages.CreatePackageScreen
import com.zzu.extrace.ui.screen.packages.ShowPackageScreen
import com.zzu.extrace.ui.screen.query.PackageSurface
import com.zzu.extrace.ui.screen.query.ExpressLayoutScreen

internal sealed class Screen(
    val route: String,
    val name: String = "",
    val icon: ImageVector = Icons.Filled.Home
) {
    object Business : Screen("business", "业务", Icons.Filled.Home)
    object Management : Screen("manage", "快件", Icons.Filled.Search)
    object Mine : Screen("mine", "包裹", Icons.Filled.MailOutline)
}

internal val Screens = listOf(
    Screen.Business,
    Screen.Management,
    Screen.Mine,
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ExtraceNavigation() {
    val navController = rememberNavController()
    ExtraceScaffold(navController = navController) { innerPadding ->
        NavHost(
            navController,
            startDestination = Screen.Business.route,
            Modifier.padding(innerPadding)
        ) {
            // Navigate
            composable(route = Screen.Business.route) {
                HomeScreen(navController)
            }
            composable(route = Screen.Management.route) {
                ExpressLayoutScreen(navController)
            }
            composable(route = Screen.Mine.route) {
                PackageSurface(navController)
            }
            // Login
            composable(route = "login") {
                LoginScreen(navController)
            }
            composable(route = "register") {
                RegisterScreen(navController)
            }
            // Express
            composable(route = "newExpress") {
                CreateExpressScreen(navController)
            }
            composable(
                route = "showExpress/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) {
                val eid = it.arguments?.getInt("id") ?: 1
                ShowExpressScreen(navController, eid)
            }
            // Package
            composable(route = "newPackage") {
                CreatePackageScreen(navController)
            }
            composable(
                route = "showPackage/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) {
                val pid = it.arguments?.getInt("id") ?: 1
                ShowPackageScreen(navController, pid)
            }
            // MapView
            composable(
                route = "map/{type}/{id}",
                arguments = listOf(
                    navArgument("id") { type = NavType.IntType },
                    navArgument("type") { type = NavType.IntType }
                )
            ) {
                val id = it.arguments?.getInt("id") ?: 1
                val type = it.arguments?.getInt("type") ?: 1
                MapScreen(navController, type, id)
            }
        }
    }
}