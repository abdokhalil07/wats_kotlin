package com.example.wtascopilot

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.wtascopilot.ui.Inspector.SmsLogModel
import com.example.wtascopilot.ui.Inspector.SmsLogScreen
import com.example.wtascopilot.ui.log.LogViewModel
import com.example.wtascopilot.ui.log.TransactionLogScreen
import com.example.wtascopilot.ui.sim.SimScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val items = listOf(Screen.Sim, Screen.Logs, Screen.Sms)

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                // الحركة دي عشان لما يتنقل ميعملش زحمة في الرامات
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        // هنا بنحدد كل route هيفتح أنهي شاشة
        NavHost(
            navController = navController,
            startDestination = Screen.Sim.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Sim.route) {
                SimScreen() // شاشة الشرائح اللي عندك
            }
            composable(Screen.Logs.route) {
                val context = androidx.compose.ui.platform.LocalContext.current
                val logViewModel: LogViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return LogViewModel(context) as T
                        }
                    })

                TransactionLogScreen(logViewModel)
            }
            composable(Screen.Sms.route) {
                val context = androidx.compose.ui.platform.LocalContext.current
                val logViewModel: SmsLogModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return SmsLogModel(context) as T
                        }
                    })

                SmsLogScreen(logViewModel)
            }
        }
    }
}