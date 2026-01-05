package com.example.wtascopilot.ui.Inspector



import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
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
import com.example.wtascopilot.data.modle.SmsTransaction


@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun SmsLogScreen(viewModel: SmsLogModel = viewModel()) {
    val smstransactions by viewModel.smstransactions.collectAsState(initial = emptyList())

    // حالة الـ Tab المختار (0 للكل أو المزامنة، 1 لغير المزامنة)
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Synced ✅", "UnSynced ⏳")

    Column {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(smstransactions) { tx ->
                TransactionItem(tx)
            }
        }
    }
}

@Composable
fun TransactionItem(tx: SmsTransaction) {
    Card(
        modifier = Modifier.padding(8.dp).fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(text = tx.sender!!, style = MaterialTheme.typography.titleMedium)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = tx.body!!, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}



