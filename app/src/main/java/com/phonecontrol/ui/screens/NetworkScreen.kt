package com.phonecontrol.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.phonecontrol.util.ShellExecutor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var networkInfo by remember { mutableStateOf("Loading...") }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        scope.launch {
            val sb = StringBuilder()

            // WiFi
            val wifiInterface = ShellExecutor.execute("getprop wifi.interface")
            sb.appendLine("WiFi Interface: ${wifiInterface.output.ifBlank { "N/A" }}")

            val wifiState = ShellExecutor.execute("getprop wifi.supplicant.state")
            sb.appendLine("WiFi State: ${wifiState.output.ifBlank { "N/A" }}")

            // DNS
            val dns1 = ShellExecutor.execute("getprop net.dns1")
            val dns2 = ShellExecutor.execute("getprop net.dns2")
            sb.appendLine("\nDNS:")
            sb.appendLine("  ${dns1.output.ifBlank { "N/A" }}")
            sb.appendLine("  ${dns2.output.ifBlank { "N/A" }}")

            // Connections
            val conns = ShellExecutor.execute("ss -tunp 2>/dev/null | head -10 || netstat -tunp 2>/dev/null | head -10")
            if (conns.output.isNotBlank()) {
                sb.appendLine("\nConnections:")
                sb.appendLine(conns.output)
            }

            networkInfo = sb.toString()
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Network") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Network Info",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                networkInfo,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                item {
                    Text(
                        "Controls",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                scope.launch { ShellExecutor.execute("cmd wifi set-wifi-enabled enabled") }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Wifi, null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("WiFi ON")
                        }
                        OutlinedButton(
                            onClick = {
                                scope.launch { ShellExecutor.execute("cmd wifi set-wifi-enabled disabled") }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.WifiOff, null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("WiFi OFF")
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                scope.launch { ShellExecutor.execute("cmd bluetooth_manager enable") }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Bluetooth, null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("BT ON")
                        }
                        OutlinedButton(
                            onClick = {
                                scope.launch { ShellExecutor.execute("cmd bluetooth_manager disable") }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.BluetoothDisabled, null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("BT OFF")
                        }
                    }
                }
            }
        }
    }
}
