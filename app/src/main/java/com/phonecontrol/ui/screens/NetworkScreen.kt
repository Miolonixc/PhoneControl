package com.phonecontrol.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.phonecontrol.util.ShellExecutor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var info by remember { mutableStateOf("Loading...") }

    fun load() {
        scope.launch {
            val sb = StringBuilder()
            val wifi = ShellExecutor.execute("getprop wifi.interface")
            sb.appendLine("WiFi Interface: ${wifi.output.ifBlank { "N/A" }}")
            val state = ShellExecutor.execute("getprop wifi.supplicant.state")
            sb.appendLine("WiFi State: ${state.output.ifBlank { "N/A" }}")
            val dns1 = ShellExecutor.execute("getprop net.dns1")
            val dns2 = ShellExecutor.execute("getprop net.dns2")
            sb.appendLine("DNS: ${dns1.output.ifBlank { "N/A" }} ${dns2.output.ifBlank { "" }}")
            val mobile = ShellExecutor.execute("getprop gsm.network.type")
            sb.appendLine("Mobile: ${mobile.output.ifBlank { "N/A" }}")
            val operator = ShellExecutor.execute("getprop gsm.operator.alpha")
            sb.appendLine("Operator: ${operator.output.ifBlank { "N/A" }}")
            info = sb.toString()
        }
    }

    LaunchedEffect(Unit) { load() }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Network") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } })
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Text(info, modifier = Modifier.padding(16.dp), fontSize = 13.sp, lineHeight = 20.sp)
            }

            Text("Controls", fontWeight = FontWeight.Bold)

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { scope.launch { ShellExecutor.execute("cmd wifi set-wifi-enabled enabled"); load() } }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                    Icon(Icons.Default.Wifi, null); Spacer(Modifier.width(4.dp)); Text("WiFi ON")
                }
                OutlinedButton(onClick = { scope.launch { ShellExecutor.execute("cmd wifi set-wifi-enabled disabled"); load() } }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                    Icon(Icons.Default.WifiOff, null); Spacer(Modifier.width(4.dp)); Text("WiFi OFF")
                }
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { scope.launch { ShellExecutor.execute("cmd bluetooth_manager enable"); load() } }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                    Icon(Icons.Default.Bluetooth, null); Spacer(Modifier.width(4.dp)); Text("BT ON")
                }
                OutlinedButton(onClick = { scope.launch { ShellExecutor.execute("cmd bluetooth_manager disable"); load() } }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                    Icon(Icons.Default.BluetoothDisabled, null); Spacer(Modifier.width(4.dp)); Text("BT OFF")
                }
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { scope.launch { ShellExecutor.execute("cmd connectivity airplane-mode enable"); load() } }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                    Icon(Icons.Default.AirplanemodeActive, null); Spacer(Modifier.width(4.dp)); Text("Airplane ON")
                }
                OutlinedButton(onClick = { scope.launch { ShellExecutor.execute("cmd connectivity airplane-mode disable"); load() } }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                    Icon(Icons.Default.AirplanemodeInactive, null); Spacer(Modifier.width(4.dp)); Text("Airplane OFF")
                }
            }
        }
    }
}
