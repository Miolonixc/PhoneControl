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
                "${modelResult.output} • Android ${androidResult.output}"
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

            items(getQuickActionsList()) { action ->
                QuickActionCard(
                    icon = action.first,
                    title = action.second,
                    subtitle = action.third,
                    color = action.fourth,
                    onClick = { onNavigate(action.fifth) }
                )
            }
        }
    }
}

@Composable
private fun getQuickActionsList(): List<Quintuple<ImageVector, String, String, Color, String>> {
    return listOf(
        Quintuple(Icons.Default.PhoneAndroid, "System Info", "Device details, battery, uptime", MaterialTheme.colorScheme.primary, "system"),
        Quintuple(Icons.Default.Apps, "Apps Manager", "Installed apps, uninstall, info", MaterialTheme.colorScheme.secondary, "apps"),
        Quintuple(Icons.Default.Build, "Tools", "Screenshot, input, display", MaterialTheme.colorScheme.tertiary, "tools"),
        Quintuple(Icons.Default.Wifi, "Network", "WiFi, Bluetooth, connections", MaterialTheme.colorScheme.error, "network"),
        Quintuple(Icons.Default.CleaningServices, "Optimizer", "Clean cache, kill background", Color(0xFF66BB6A), "optimize"),
        Quintuple(Icons.Default.Terminal, "Terminal", "Run shell commands", Color(0xFF78909C), "terminal")
    )
}

data class Quintuple<A, B, C, D, E>(val first: A, val second: B, val third: C, val fourth: D, val fifth: E)

@Composable
fun DeviceInfoCard(deviceInfo: String, ramInfo: String, storageInfo: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Device",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                deviceInfo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
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
fun InfoChip(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun QuickActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .animateContentSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
