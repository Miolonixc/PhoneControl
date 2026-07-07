package com.phonecontrol.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
fun AppsScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var apps by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var filter by remember { mutableStateOf("third_party") }
    var showDialog by remember { mutableStateOf(false) }
    var selectedApp by remember { mutableStateOf("") }

    LaunchedEffect(filter) {
        isLoading = true
        scope.launch {
            val flag = when (filter) {
                "system" -> "-s"
                "running" -> "-e"
                else -> "-3"
            }
            val result = ShellExecutor.execute("pm list packages $flag | sed 's/package://' | sort")
            apps = if (result.success) {
                result.output.split("\n").filter { it.isNotBlank() }
            } else emptyList()
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Apps Manager") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Filter chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filter == "third_party",
                    onClick = { filter = "third_party" },
                    label = { Text("Third Party") }
                )
                FilterChip(
                    selected = filter == "system",
                    onClick = { filter = "system" },
                    label = { Text("System") }
                )
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(apps) { app ->
                        AppItem(
                            packageName = app,
                            onUninstall = {
                                selectedApp = app
                                showDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Uninstall App") },
            text = { Text("Uninstall $selectedApp?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            ShellExecutor.execute("pm uninstall $selectedApp")
                        }
                        showDialog = false
                    }
                ) {
                    Text("Uninstall", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AppItem(packageName: String, onUninstall: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Apps,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    packageName,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expand"
                    )
                }
            }

            if (expanded) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AssistChip(
                        onClick = {
                            onUninstall()
                        },
                        label = { Text("Uninstall") },
                        leadingIcon = {
                            Icon(Icons.Default.Delete, null, Modifier.size(18.dp))
                        }
                    )
                    AssistChip(
                        onClick = { /* Force stop */ },
                        label = { Text("Force Stop") },
                        leadingIcon = {
                            Icon(Icons.Default.Stop, null, Modifier.size(18.dp))
                        }
                    )
                }
            }
        }
    }
}
