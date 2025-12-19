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

@Composable
fun SimScreen(
    viewModel: SimViewModel,
    onSimSelected: () -> Unit
) {
    val context = LocalContext.current
    val state = viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadSims(context)
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("اختار الخط اللي هيتربط بالحساب", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(state.value.sims) { sim ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("الشبكة: ${sim.carrierName}")
                        Text("الرقم: ${sim.number}")

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                viewModel.selectSim(sim.number)
                                // هنا كمان نسجّل في SimStorage
                                SimStorage.saveSim(context, sim.number)
                                onSimSelected()
                            }
                        ) {
                            Text("ربط هذا الخط")
                        }
                    }
                }
            }
        }
    }
}
