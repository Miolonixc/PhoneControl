package com.phonecontrol.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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

data class AppInfo(val pkg: String, val label: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppsScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var apps by remember { mutableStateOf(listOf<AppInfo>()) }
    var loading by remember { mutableStateOf(true) }
    var filter by remember { mutableIntStateOf(0) }
    var search by remember { mutableStateOf("") }
    var showConfirm by remember { mutableStateOf(false) }
    var targetPkg by remember { mutableStateOf("") }

    LaunchedEffect(filter) {
        loading = true
        scope.launch {
            val flag = when (filter) { 0 -> "-3"; 1 -> "-s"; 2 -> "-e"; else -> "-3" }
            val result = ShellExecutor.execute("pm list packages $flag | sed 's/package://' | sort")
            val pkgs = if (result.success) result.output.lines().filter { it.isNotBlank() } else emptyList()

            apps = pkgs.map { pkg ->
                val labelResult = ShellExecutor.execute("dumpsys package $pkg | grep -A1 'label=' | grep 'label=' | sed 's/.*label=//' | head -1")
                val label = if (labelResult.success && labelResult.output.isNotBlank()) {
                    labelResult.output.trim().substringBefore("\n").trim()
                } else {
                    // Fallback: extract last part of package name
                    pkg.substringAfterLast(".")
                        .replaceFirstChar { it.uppercase() }
                }
                AppInfo(pkg, label)
            }
            loading = false
        }
    }

    val filtered = if (search.isBlank()) apps
    else apps.filter { it.pkg.contains(search, ignoreCase = true) || it.label.contains(search, ignoreCase = true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Apps") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search apps...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                trailingIcon = { if (search.isNotBlank()) IconButton(onClick = { search = "" }) { Icon(Icons.Default.Clear, null) } else null },
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            Row(Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val labels = listOf("Third-party", "System", "Enabled")
                labels.forEachIndexed { i, label ->
                    FilterChip(selected = filter == i, onClick = { filter = i }, label = { Text(label, fontSize = 12.sp) })
                }
            }

            if (loading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else {
                Text("  ${filtered.size} apps", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 4.dp))
                LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(filtered) { app ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Apps, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(app.label, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                Text(app.pkg, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            IconButton(onClick = { targetPkg = app.pkg; showConfirm = true }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.DeleteOutline, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text("Uninstall?") },
            text = { Text(targetPkg) },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch { ShellExecutor.execute("pm uninstall $targetPkg") }
                    showConfirm = false
                }) { Text("Uninstall", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { showConfirm = false }) { Text("Cancel") } }
        )
    }
}
