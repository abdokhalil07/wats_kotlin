package com.example.wtascopilot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.wtascopilot.ui.login.LoginScreen
import com.example.wtascopilot.ui.login.LoginViewModel
import com.example.wtascopilot.ui.sim.SimScreen
import com.example.wtascopilot.ui.sim.SimViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

                // 1. إعداد الـ NavController
                val navController = rememberNavController()
                val context = LocalContext.current
                var simSelected by remember { mutableStateOf(false) }

                // 2. تحديد شاشة البداية بناءً على هل المستخدم مسجل دخول أم لا
                // إذا كان مسجل دخول -> "home"، غير ذلك -> "login"
                val startDestination = if (UserStorage.isUserLoggedIn(context)) "home" else "login"

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    // 3. إعداد الـ NavHost
                    NavHost(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = Modifier.padding(innerPadding)
                    ) {

                        // --- شاشة تسجيل الدخول ---
                        composable("login") {
                            // إنشاء ViewModel يدوياً لتمرير الـ Repository
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
                                    // عند النجاح، نذهب للشاشة الرئيسية ونحذف شاشة الدخول من التاريخ (BackStack)
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // --- الشاشة الرئيسية ---
                        composable("home") {
                            // يمكنك هنا أيضاً استخدام Factory إذا كان SimViewModel يحتاج لـ Repository
                            // سأفترض هنا أنه يعمل بدون Factory أو يمكنك إضافته بنفس طريقة LoginViewModel

                            val simViewModel = remember { SimViewModel() }

                            SimScreen(
                                viewModel = simViewModel,
                                onSimSelected = { simSelected = true }
                            )
                        }
                    }
                }

        }
    }
}