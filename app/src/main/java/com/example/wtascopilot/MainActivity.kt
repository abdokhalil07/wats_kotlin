package com.example.wtascopilot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.wtascopilot.data.repository.LoginRepository
import com.example.wtascopilot.ui.login.LoginScreen
import com.example.wtascopilot.ui.login.LoginViewModel
import com.example.wtascopilot.ui.sim.SimScreen
import com.example.wtascopilot.ui.sim.SimViewModel
import com.example.wtascopilot.ui.theme.WTASCopilotTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            var loggedIn by remember { mutableStateOf(false) }
            var simSelected by remember { mutableStateOf(false) }

            if (!loggedIn) {
                val loginViewModel = remember { LoginViewModel(LoginRepository()) }

                LoginScreen(
                    viewModel = loginViewModel,
                    onLoginSuccess = { loggedIn = true }
                )

            } else if (!simSelected) {
                val simViewModel = remember { SimViewModel() }

                SimScreen(
                    viewModel = simViewModel,
                    onSimSelected = { simSelected = true }
                )

            } else {
                // هنا تحط الشاشة الأساسية بعد الربط (Dashboard مثلاً)
                Text("Ready – Logged in & SIM linked")
            }
        }
    }
}

