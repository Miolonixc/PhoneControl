package com.phonecontrol.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.phonecontrol.util.ShellExecutor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SystemInfoScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var props by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        scope.launch {
            val commands = listOf(
                "getprop ro.product.model" to "Model",
                "getprop ro.product.brand" to "Brand",
                "getprop ro.product.device" to "Device",
                "getprop ro.board.platform" to "SoC",
                "getprop ro.build.version.release" to "Android",
                "getprop ro.build.version.sdk" to "SDK",
                "getprop ro.build.display.id" to "Build",
                "getprop ro.build.type" to "Build Type",
                "getprop ro.serialno" to "Serial",
                "getprop persist.sys.timezone" to "Timezone",
                "getprop persist.sys.locale" to "Locale",
                "cat /proc/version" to "Kernel",
                "cat /proc/cpuinfo | grep 'model name' | head -1" to "CPU",
                "df -h /data | tail -1 | awk '{print \$2, \$3, \$4, \$5}'" to "Storage",
                "cat /proc/meminfo | head -1" to "Total RAM"
            )

            val results = commands.map { (cmd, label) ->
                val result = ShellExecutor.execute(cmd)
                label to if (result.success) result.output.trim() else "N/A"
            }

            props = results
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("System Info") },
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(props) { (label, value) ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                label,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                value,
                                style = MaterialTheme.typography.bodyMedium,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
