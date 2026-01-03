package com.example.wtascopilot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wtascopilot.data.local.UserStorage
import com.example.wtascopilot.data.work.WorkScheduler
import com.example.wtascopilot.ui.login.LoginScreen
import com.example.wtascopilot.ui.login.LoginViewModel
import com.example.wtascopilot.ui.login.LoginScreen
import com.example.wtascopilot.data.repository.LoginRepository
// هذا السطر مهم جداً لاستدعاء الدالة التي أنشأتها في الملف المنفصل
import com.example.wtascopilot.MainNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WorkScheduler.scheduleSync(this)
        setContent {

            val navController = rememberNavController()
            val context = LocalContext.current
            val isLoggedIn = UserStorage.getAccountId(context) != -1

            NavHost(
                navController = navController,
                startDestination = if (isLoggedIn) "main_home" else "login"
            ) {
                // --- هـنا مـنـطق الـ Login بـتاعك زي ما هـو بـالظبط ---
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
                            navController.navigate("main_home") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    )
                }

                // --- ده الـ Route اللي بيفتح الـ Navigation Bar بعد الدخول ---
                composable("main_home") {
                    MainNavigation() // الدالة اللي أنت عملتها في ملف منفصل
                }
            }
        }

        checkAndRequestPermissions()
        requestIgnoreBatteryOptimizations()
}
    private fun checkAndRequestPermissions() {
        val permissions = arrayOf(
            android.Manifest.permission.RECEIVE_SMS,
            android.Manifest.permission.READ_PHONE_STATE
        )

        val permissionsToRequest = permissions.filter {
            androidx.core.content.ContextCompat.checkSelfPermission(this, it) != android.content.pm.PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            androidx.core.app.ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                100 // Request Code
            )
        }
    }

    fun requestIgnoreBatteryOptimizations() {
        val intent = android.content.Intent()
        val packageName = packageName
        val pm = getSystemService(android.content.Context.POWER_SERVICE) as android.os.PowerManager

        // بنشيك هل التطبيق فعلاً مستثنى ولا لأ
        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            intent.action = android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            intent.data = android.net.Uri.parse("package:$packageName")
            startActivity(intent)
        }
    }
}
