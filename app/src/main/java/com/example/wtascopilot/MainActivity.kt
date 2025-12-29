package com.example.wtascopilot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wtascopilot.data.local.UserStorage
import com.example.wtascopilot.data.repository.LoginRepository
import com.example.wtascopilot.data.work.WorkScheduler
import com.example.wtascopilot.ui.login.LoginScreen
import com.example.wtascopilot.ui.login.LoginViewModel
import com.example.wtascopilot.ui.sim.SimScreen
import com.example.wtascopilot.ui.sim.SimViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WorkScheduler.scheduleSync(this)
        setContent {
                val navController = rememberNavController()
                val context = LocalContext.current

                val startDestination = if (UserStorage.isUserLoggedIn(context)) "home" else "login"

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("login") {
                            val loginViewModel: LoginViewModel = viewModel(
                                factory = object : ViewModelProvider.Factory {
                                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                        return LoginViewModel(LoginRepository()) as T
                                    }
                                }
                            )
                            LoginScreen(
                                viewModel = loginViewModel,
                                onLoginSuccess = {
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("home") {
                            val simViewModel: SimViewModel = viewModel()

                            SimScreen(
                                viewModel = simViewModel,
                                onSimSelected = { }
                            )
                        }
                    }
                }

        }
    }
}