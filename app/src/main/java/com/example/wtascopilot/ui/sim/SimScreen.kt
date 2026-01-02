package com.example.wtascopilot.ui.sim


import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SimScreen(
    viewModel: SimViewModel = viewModel(),
    onSimSelected: () -> Unit = {}
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // 1. تعريف الأذونات المطلوبة
    val permissionsToRequest = remember {
        mutableListOf(
            Manifest.permission.READ_PHONE_STATE
        ).apply {
            // إضافة إذن قراءة الأرقام للأندرويد الحديث
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                add(Manifest.permission.READ_PHONE_NUMBERS)
            }
        }.toTypedArray()
    }

    // 2. مجهز (Launcher) لطلب الأذونات والتعامل مع النتيجة
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            // لو وافق، حمل البيانات
            viewModel.loadSimCards(context)
        } else {
            // لو رفض، أظهر رسالة
            Toast.makeText(context, "يجب الموافقة على الأذونات لعرض الشرائح", Toast.LENGTH_LONG).show()
        }
    }

    // 3. التحقق والطلب عند فتح الشاشة
    LaunchedEffect(Unit) {
        val allGranted = permissionsToRequest.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

        if (allGranted) {
            viewModel.loadSimCards(context)
        } else {
            permissionLauncher.launch(permissionsToRequest)
        }
    }

    Scaffold { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Text(
                text = "إدارة الشرائح",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.simCards) { simUiModel ->
                    SimCardItem(
                        simModel = simUiModel,
                        onButtonClick = {
                            // 2. عند الضغط على الزر، ننفذ المنطق الموجود
                            viewModel.toggleSimRegistration(context, simUiModel)

                            // 3. (اختياري) إذا كنت تريد الانتقال للشاشة التالية فور الضغط على تفعيل:
                            if (!simUiModel.isRegistered) { // يعني هو ضغط عشان يسجل
                                onSimSelected()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SimCardItem(simModel: SimUiModel, onButtonClick: () -> Unit) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = simModel.simInfo.carrierName,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = simModel.simInfo.phoneNumber,
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Subscription ID: ${simModel.simInfo.subscriptionId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )

                // حالة التسجيل
                Text(
                    text = if (simModel.isRegistered) "مسجلة" else "غير مسجلة",
                    color = if (simModel.isRegistered) Color(0xFF4CAF50) else Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // زر التفعيل / الإيقاف
            Button(
                onClick = onButtonClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (simModel.isRegistered) Color.Red else MaterialTheme.colorScheme.primary
                )
            ) {
                Text(text = if (simModel.isRegistered) "Stop" else "تفعيل")
            }
        }
    }
}