package com.phonecontrol.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.phonecontrol.util.ShellExecutor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private data class CardData(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val gradient: List<Color>,
    val route: String
)

private val cardList = listOf(
    CardData(Icons.Outlined.PhoneAndroid, "System", "Device info", listOf(Color(0xFF1976D2), Color(0xFF42A5F5)), "system"),
    CardData(Icons.Outlined.Apps, "Apps", "Manager", listOf(Color(0xFF388E3C), Color(0xFF66BB6A)), "apps"),
    CardData(Icons.Outlined.Build, "Tools", "Screenshot & input", listOf(Color(0xFFF57C00), Color(0xFFFFB74D)), "tools"),
    CardData(Icons.Outlined.Wifi, "Network", "WiFi & BT", listOf(Color(0xFF7B1FA2), Color(0xFFBA68C8)), "network"),
    CardData(Icons.Outlined.Speed, "Optimize", "Clean & boost", listOf(Color(0xFFD32F2F), Color(0xFFEF5350)), "optimize"),
    CardData(Icons.Outlined.Terminal, "Terminal", "Shell commands", listOf(Color(0xFF455A64), Color(0xFF78909C)), "terminal")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(onNavigate: (String) -> Unit) {
    val scope = rememberCoroutineScope()
    var model by remember { mutableStateOf("") }
    var android by remember { mutableStateOf("") }
    var soc by remember { mutableStateOf("") }
    var ramUsed by remember { mutableIntStateOf(0) }
    var ramTotal by remember { mutableIntStateOf(0) }
    var storagePct by remember { mutableIntStateOf(0) }
    var storageFree by remember { mutableStateOf("") }
    var batteryLevel by remember { mutableIntStateOf(-1) }

    LaunchedEffect(Unit) {
        scope.launch {
            model = ShellExecutor.execute("getprop ro.product.model").output.ifBlank { "Unknown" }
            android = ShellExecutor.execute("getprop ro.build.version.release").output.ifBlank { "?" }
            soc = ShellExecutor.execute("getprop ro.board.platform").output.ifBlank { "?" }

            val memResult = ShellExecutor.execute("cat /proc/meminfo | head -5")
            if (memResult.success) {
                val lines = memResult.output.lines()
                val total = lines.getOrNull(0)?.let { Regex("(\\d+)").find(it)?.groupValues?.get(1)?.toIntOrNull() } ?: 0
                val avail = lines.getOrNull(2)?.let { Regex("(\\d+)").find(it)?.groupValues?.get(1)?.toIntOrNull() } ?: 0
                ramTotal = total / 1024
                ramUsed = (total - avail) / 1024
            }

            val dfResult = ShellExecutor.execute("df /data | tail -1")
            if (dfResult.success) {
                val parts = dfResult.output.split("\\s+".toRegex())
                val used = parts.getOrNull(2)?.toLongOrNull() ?: 0
                val avail = parts.getOrNull(3)?.toLongOrNull() ?: 0
                val total = used + avail
                storagePct = if (total > 0) ((used * 100) / total).toInt() else 0
                storageFree = "${avail / 1048576} GB free"
            }

            val battResult = ShellExecutor.execute("cat /sys/class/power_supply/battery/capacity 2>/dev/null")
            if (battResult.success) batteryLevel = battResult.output.trim().toIntOrNull() ?: -1
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Phone Control", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(span = { GridItemSpan(2) }) {
                DeviceCard(model, android, soc, ramUsed, ramTotal, storagePct, storageFree, batteryLevel)
            }

            items(cardList) { card ->
                GradientCard(
                    icon = card.icon,
                    title = card.title,
                    subtitle = card.subtitle,
                    gradient = card.gradient,
                    onClick = { onNavigate(card.route) }
                )
            }
        }
    }
}

@Composable
private fun DeviceCard(
    model: String, android: String, soc: String,
    ramUsed: Int, ramTotal: Int,
    storagePct: Int, storageFree: String,
    batteryLevel: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(Color(0xFF1565C0), Color(0xFF0D47A1))))
                .padding(20.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.PhoneAndroid, null, tint = Color.White, modifier = Modifier.size(28.dp))
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(model, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Android $android  \u00b7  $soc", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                    }
                }

                Spacer(Modifier.height(16.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatBox(Modifier.weight(1f), "RAM", "${ramUsed}/${ramTotal} MB", if (ramTotal > 0) ramUsed * 100 / ramTotal else 0)
                    StatBox(Modifier.weight(1f), "Storage", storageFree, storagePct)
                    if (batteryLevel >= 0) {
                        StatBox(Modifier.weight(1f), "Battery", "$batteryLevel%", batteryLevel)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatBox(modifier: Modifier, label: String, value: String, pct: Int) {
    Column(
        modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.12f))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
        Spacer(Modifier.height(2.dp))
        Text(value, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { (pct.coerceIn(0, 100) / 100f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = when {
                pct > 85 -> Color(0xFFEF5350)
                pct > 60 -> Color(0xFFFFB74D)
                else -> Color(0xFF66BB6A)
            },
            trackColor = Color.White.copy(alpha = 0.1f)
        )
    }
}

@Composable
private fun GradientCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    gradient: List<Color>,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(gradient))
                .padding(16.dp)
        ) {
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Column {
                    Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(subtitle, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                }
            }
        }
    }
}
