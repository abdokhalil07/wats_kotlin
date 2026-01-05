package com.example.wtascopilot

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SimCard
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Sms

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Sim : Screen("sim", "الشرائح", Icons.Default.SimCard)
    object Logs : Screen("logs", "السجلات", Icons.Default.ListAlt)

    object Sms : Screen("Sms", "الرسائل", Icons.Default.Sms)
}