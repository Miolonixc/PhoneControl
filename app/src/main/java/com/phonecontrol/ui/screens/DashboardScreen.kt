package com.phonecontrol.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.phonecontrol.util.ShellExecutor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigate: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    var ramInfo by remember { mutableStateOf("Loading...") }
    var storageInfo by remember { mutableStateOf("Loading...") }
    var deviceInfo by remember { mutableStateOf("Loading...") }

    LaunchedEffect(Unit) {
        scope.launch {
            val memResult = ShellExecutor.execute("cat /proc/meminfo | head -3")
            ramInfo = if (memResult.success) memResult.output else "N/A"

            val storageResult = ShellExecutor.execute("df -h /data | tail -1")
            storageInfo = if (storageResult.success) storageResult.output else "N/A"

            val modelResult = ShellExecutor.execute("getprop ro.product.model")
            val androidResult = ShellExecutor.execute("getprop ro.build.version.release")
            deviceInfo = if (modelResult.success && androidResult.success) {
                "${modelResult.output} \u2022 Android ${androidResult.output}"
            } else "N/A"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Phone Control", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
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
                DeviceInfoCard(deviceInfo, ramInfo, storageInfo)
            }

            item {
                Text(
                    "Quick Actions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    QuickActionCard(Modifier.weight(1f), Icons.Default.PhoneAndroid, "System Info", MaterialTheme.colorScheme.primary) { onNavigate("system") }
                    QuickActionCard(Modifier.weight(1f), Icons.Default.Apps, "Apps", MaterialTheme.colorScheme.secondary) { onNavigate("apps") }
                }
            }
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    QuickActionCard(Modifier.weight(1f), Icons.Default.Build, "Tools", MaterialTheme.colorScheme.tertiary) { onNavigate("tools") }
                    QuickActionCard(Modifier.weight(1f), Icons.Default.Wifi, "Network", MaterialTheme.colorScheme.error) { onNavigate("network") }
                }
            }
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    QuickActionCard(Modifier.weight(1f), Icons.Default.CleaningServices, "Optimizer", Color(0xFF66BB6A)) { onNavigate("optimize") }
                    QuickActionCard(Modifier.weight(1f), Icons.Default.Terminal, "Terminal", Color(0xFF78909C)) { onNavigate("terminal") }
                }
            }
        }
    }
}

@Composable
private fun DeviceInfoCard(deviceInfo: String, ramInfo: String, storageInfo: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Device", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(deviceInfo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                InfoChip("RAM", ramInfo.split("\n").getOrNull(1)?.trim()?.take(20) ?: "N/A", Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))
                InfoChip("Storage", storageInfo.split(" ").getOrNull(4) ?: "N/A", Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun InfoChip(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(modifier = modifier, shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surface) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun QuickActionCard(
    modifier: Modifier,
    icon: ImageVector,
    title: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).animateContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = color, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
        }
    }
}
