package com.phonecontrol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.phonecontrol.ui.screens.*
import com.phonecontrol.ui.theme.PhoneControlTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PhoneControlTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PhoneControlApp()
                }
            }
        }
    }
}

@Composable
fun PhoneControlApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "dashboard"
    ) {
        composable("dashboard") {
            DashboardScreen(
                onNavigate = { route ->
                    navController.navigate(route)
                }
            )
        }

        composable("system") {
            SystemInfoScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable("apps") {
            AppsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable("tools") {
            ToolsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable("network") {
            NetworkScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable("optimize") {
            OptimizeScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable("terminal") {
            TerminalScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
