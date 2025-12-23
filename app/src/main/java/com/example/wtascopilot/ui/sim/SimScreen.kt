package com.example.wtascopilot.ui.sim

import android.Manifest
import androidx.compose.foundation.background
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
import androidx.lifecycle.viewmodel.compose.viewModel // تأكد من وجود هذا الـ import

@Composable
fun SimScreen(viewModel: SimViewModel = viewModel()) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // تحميل البيانات عند فتح الشاشة
    LaunchedEffect(Unit) {
        viewModel.loadSimCards(context)
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
                            viewModel.toggleSimRegistration(context, simUiModel)
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
                // المطلب رقم 1: كتابة الحالة
                Text(
                    text = if (simModel.isRegistered) "✅ مسجلة في النظام" else "❌ غير مسجلة",
                    color = if (simModel.isRegistered) Color(0xFF4CAF50) else Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // المطلب 2 و 3: الزر المتغير
            Button(
                onClick = onButtonClick,
                colors = ButtonDefaults.buttonColors(
                    // لو مسجلة (عايزين نعمل Stop) يبقي أحمر، لو مش مسجلة (تسجيل) يبقي أزرق
                    containerColor = if (simModel.isRegistered) Color.Red else MaterialTheme.colorScheme.primary
                )
            ) {
                Text(text = if (simModel.isRegistered) "Stop" else "تفعيل")
            }
        }
    }
}