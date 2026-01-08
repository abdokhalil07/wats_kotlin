package com.example.wtascopilot

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wtascopilot.data.local.UserStorage
import com.example.wtascopilot.data.repository.LoginRepository
import com.example.wtascopilot.data.work.WorkScheduler
import com.example.wtascopilot.foreground.SmsMonitorService
import com.example.wtascopilot.navigation.MainNavigation
import com.example.wtascopilot.ui.login.LoginScreen
import com.example.wtascopilot.ui.login.LoginViewModel

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
        }
        startSmsService()
        checkAndRequestPermissions()
        requestIgnoreBatteryOptimizations()
}
    private fun checkAndRequestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_PHONE_STATE
        )

        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                100 // Request Code
            )
        }
    }

    fun startSmsService() {
        val intent = Intent(this, SmsMonitorService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    fun requestIgnoreBatteryOptimizations() {
        val intent = Intent()
        val packageName = packageName
        val pm = getSystemService(POWER_SERVICE) as PowerManager

        // بنشيك هل التطبيق فعلاً مستثنى ولا لأ
        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        }
    }
}