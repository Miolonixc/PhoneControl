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
fun ToolsScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var lastResult by remember { mutableStateOf("") }

    fun runCommand(cmd: String) {
        scope.launch {
            val result = ShellExecutor.execute(cmd)
            lastResult = if (result.success) result.output else result.error
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tools") },
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
                Text(
                    "Screenshot",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Button(
                    onClick = {
                        runCommand("screencap -p /sdcard/Download/screenshot_\$(date +%Y%m%d_%H%M%S).png")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.CameraAlt, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Take Screenshot")
                }
            }

            item {
                HorizontalDivider()
                Text(
                    "Display",
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
                        onClick = { runCommand("cmd input keyevent 26") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.ScreenLockPortrait, null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Screen Off")
                    }
                    OutlinedButton(
                        onClick = { runCommand("cmd input keyevent 224") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.ScreenLockLandscape, null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Screen On")
                    }
                }
            }

            item {
                HorizontalDivider()
                Text(
                    "Input Simulation",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Text(
                    "Note: Input requires INJECT_EVENTS permission or accessibility service",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AssistChip(
                        onClick = { runCommand("cmd input keyevent 3") },
                        label = { Text("Home") }
                    )
                    AssistChip(
                        onClick = { runCommand("cmd input keyevent 4") },
                        label = { Text("Back") }
                    )
                    AssistChip(
                        onClick = { runCommand("cmd input keyevent 26") },
                        label = { Text("Power") }
                    )
                }
            }

            item {
                HorizontalDivider()
                Text(
                    "Output",
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
                    Text(
                        text = lastResult.ifBlank { "No output yet" },
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
