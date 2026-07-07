package com.phonecontrol.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.phonecontrol.util.ShellExecutor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptimizeScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var status by remember { mutableStateOf("Ready") }
    var isRunning by remember { mutableStateOf(false) }
    var results by remember { mutableStateOf<List<String>>(emptyList()) }

    fun addResult(msg: String) {
        results = results + msg
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Optimizer") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isRunning)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isRunning) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        } else {
                            Icon(
                                Icons.Default.Speed,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(status, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        isRunning = true
                        status = "Optimizing..."
                        results = emptyList()

                        scope.launch {
                            // Kill background
                            addResult("Killing background processes...")
                            val killResult = ShellExecutor.execute("am kill-all")
                            addResult("  Background killed: ${killResult.success}")

                            // Clean cache
                            addResult("Cleaning caches...")
                            val cacheResult = ShellExecutor.execute(
                                "find /data/data -maxdepth 2 -type d -name 'cache' -exec rm -rf {}/* \\; 2>/dev/null"
                            )
                            addResult("  Caches cleaned: ${cacheResult.success}")

                            // Clean logs
                            addResult("Cleaning logs...")
                            val logResult = ShellExecutor.execute(
                                "find /data/data -name '*.log' -type f -delete 2>/dev/null"
                            )
                            addResult("  Logs cleaned: ${logResult.success}")

                            // Clean temp
                            addResult("Cleaning temp files...")
                            val tmpResult = ShellExecutor.execute("find /tmp -type f -delete 2>/dev/null")
                            addResult("  Temp cleaned: ${tmpResult.success}")

                            status = "Done!"
                            isRunning = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isRunning
                ) {
                    Icon(Icons.Default.AutoFixHigh, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Auto Optimize")
                }
            }

            item {
                HorizontalDivider()
                Text(
                    "Quick Actions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                val result = ShellExecutor.execute("am kill-all")
                                addResult("Background killed: ${result.success}")
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Memory, null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Kill BG")
                    }
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                val result = ShellExecutor.execute("find /data/data -maxdepth 2 -type d -name 'cache' -exec rm -rf {}/* \\; 2>/dev/null")
                                addResult("Cache cleaned: ${result.success}")
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.CleaningServices, null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Clean")
                    }
                }
            }

            if (results.isNotEmpty()) {
                item {
                    HorizontalDivider()
                    Text(
                        "Log",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            results.forEach { line ->
                                Text(
                                    line,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
