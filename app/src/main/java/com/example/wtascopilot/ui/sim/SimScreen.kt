package com.example.wtascopilot.ui.sim

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.wtascopilot.data.local.SimStorage
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat



@Composable
fun SimScreen(
    viewModel: SimViewModel,
    onSimSelected: () -> Unit
) {
    val context = LocalContext.current
    val state = viewModel.uiState.collectAsState()

    // 1. إعداد مطلق الإذن (Permission Launcher)
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val isGranted = permissions.values.all { it }
            if (isGranted) {
                // ✅ هام: تحميل البيانات فوراً بعد الموافقة
                viewModel.loadSims(context)
            } else {
                // يمكن هنا عرض رسالة توضح أن الإذن ضروري
            }
        }
    )

    LaunchedEffect(Unit) {
        // 2. التحقق من الأذونات عند فتح الشاشة
        val hasPermission = androidx.core.content.ContextCompat.checkSelfPermission(
            context, android.Manifest.permission.READ_PHONE_NUMBERS
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED &&
                androidx.core.content.ContextCompat.checkSelfPermission(
                    context, android.Manifest.permission.READ_PHONE_STATE
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            viewModel.loadSims(context)
        } else {
            // طلب الإذن إذا لم يكن موجوداً
            permissionLauncher.launch(
                arrayOf(
                    android.Manifest.permission.READ_PHONE_NUMBERS,
                    android.Manifest.permission.READ_PHONE_STATE
                )
            )
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("اختار الخط اللي هيتربط بالحساب", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        // عرض رسالة إذا كانت القائمة فارغة بعد التحميل
        if (state.value.sims.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("لا توجد شرائح متاحة أو لم يتم منح الإذن", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn {
                items(state.value.sims) { sim ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("الشبكة: ${sim.carrierName}")
                            // عرض رسالة بديلة إذا الرقم غير موجود
                            Text("الرقم: ${if (sim.number.isNotEmpty()) sim.number else "غير معروف"}")

                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = {
                                viewModel.selectSim(sim.number)
                                SimStorage.saveSim(context, sim.number)
                                onSimSelected()
                            }) {
                                Text("ربط هذا الخط")
                            }
                        }
                    }
                }
            }
        }
    }
}
