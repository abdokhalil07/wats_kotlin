package com.example.wtascopilot.ui.log


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.SimCard
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wtascopilot.data.modle.Transaction

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun TransactionLogScreen(viewModel: LogViewModel = viewModel()) {
    val transactions by viewModel.transactions.collectAsState(initial = emptyList())

    // حالة الـ Tab المختار (0 للكل أو المزامنة، 1 لغير المزامنة)
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Synced ✅", "UnSynced ⏳")

    Column {
        // شريط التبويب (Tabs)
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }

        // تصفية القائمة بناءً على الـ Tab
        val filteredList = when (selectedTabIndex) {
            0 -> transactions.filter { it.isSynced == 1 }
            1 -> transactions.filter { it.isSynced == 0 }
            else -> transactions
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(filteredList) { tx ->
                TransactionItem(tx)
            }
        }
    }
}

@Composable
fun TransactionItem(tx: Transaction) {
    Card(
        modifier = Modifier.padding(8.dp).fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(text = tx.transactionType, style = MaterialTheme.typography.titleMedium)
                Text(text = "${tx.amount} EGP", color = Color.Green, style = MaterialTheme.typography.titleLarge)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // إضافة رقم الشريحة هنا
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.SimCard, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "SIM: ${tx.simNumber}", style = MaterialTheme.typography.bodySmall)
                // ملحوظة: لو مش مخزن رقم الشريحة، ممكن نعرض الـ slotId
            }

            Text(text = tx.dateTime, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}
