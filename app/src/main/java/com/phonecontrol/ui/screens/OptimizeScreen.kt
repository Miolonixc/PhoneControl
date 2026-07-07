package com.phonecontrol.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.phonecontrol.util.ShellExecutor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptimizeScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var status by remember { mutableStateOf("Ready") }
    var running by remember { mutableStateOf(false) }
    var log by remember { mutableStateOf(listOf<String>()) }

    fun addLog(msg: String) { log = log + msg }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Optimizer") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } })
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = if (running) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (running) CircularProgressIndicator(Modifier.size(24.dp))
                    else Icon(Icons.Default.Speed, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(12.dp))
                    Text(status, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
            }

            Button(
                onClick = {
                    running = true; status = "Optimizing..."; log = emptyList()
                    scope.launch {
                        addLog("Killing background apps...")
                        ShellExecutor.execute("am kill-all")
                        addLog("Clearing app caches...")
                        ShellExecutor.execute("find /data/data -maxdepth 2 -type d -name cache -exec rm -rf {}/* \\; 2>/dev/null")
                        addLog("Cleaning log files...")
                        ShellExecutor.execute("find /data/data -name '*.log' -type f -delete 2>/dev/null")
                        addLog("Cleaning temp files...")
                        ShellExecutor.execute("find /tmp -type f -delete 2>/dev/null")
                        addLog("Done!")
                        status = "Complete"
                        running = false
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                enabled = !running
            ) {
                Icon(Icons.Default.AutoFixHigh, null)
                Spacer(Modifier.width(8.dp))
                Text("Auto Optimize", fontWeight = FontWeight.Bold)
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { scope.launch { addLog("Killed: ${ShellExecutor.execute("am kill-all").success}") } },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Memory, null, Modifier.size(18.dp)); Spacer(Modifier.width(4.dp)); Text("Kill BG")
                }
                OutlinedButton(
                    onClick = { scope.launch { ShellExecutor.execute("find /data/data -maxdepth 2 -type d -name cache -exec rm -rf {}/* \\; 2>/dev/null"); addLog("Cache cleaned") } },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.CleaningServices, null, Modifier.size(18.dp)); Spacer(Modifier.width(4.dp)); Text("Clean Cache")
                }
            }

            if (log.isNotEmpty()) {
                Text("Log", fontWeight = FontWeight.Bold)
                Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(Modifier.padding(12.dp)) {
                        log.forEach { Text(it, fontSize = 12.sp, lineHeight = 18.sp) }
                    }
                }
            }
        }
    }
}
