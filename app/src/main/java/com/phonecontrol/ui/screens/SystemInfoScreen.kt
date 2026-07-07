package com.phonecontrol.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
    var info by remember { mutableStateOf(listOf<Pair<String, String>>()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        scope.launch {
            val cmds = listOf(
                "Model" to "getprop ro.product.model",
                "Brand" to "getprop ro.product.brand",
                "Device" to "getprop ro.product.device",
                "SoC Platform" to "getprop ro.board.platform",
                "CPU" to "getprop ro.hardware",
                "Android Version" to "getprop ro.build.version.release",
                "SDK Level" to "getprop ro.build.version.sdk",
                "Build ID" to "getprop ro.build.display.id",
                "Build Type" to "getprop ro.build.type",
                "Serial" to "getprop ro.serialno",
                "Timezone" to "getprop persist.sys.timezone",
                "Locale" to "getprop persist.sys.locale",
                "Kernel" to "uname -r",
                "Screen" to "cmd display get-displays 2>/dev/null | grep -oE 'real [0-9]+ x [0-9]+' | head -1",
                "Total RAM" to "awk '/MemTotal/{printf \"%.1f GB\", \$2/1048576}' /proc/meminfo",
                "Available RAM" to "awk '/MemAvailable/{printf \"%.1f GB\", \$2/1048576}' /proc/meminfo",
                "Storage" to "df -h /data | tail -1 | awk '{printf \"%s used / %s total (%s avail)\", \$3, \$2, \$4}'",
                "Uptime" to "awk '{d=int(\$1/86400); h=int(\$1%86400/3600); m=int(\$1%3600/60); printf \"%dd %dh %dm\",d,h,m}' /proc/uptime 2>/dev/null",
                "Encryption" to "getprop ro.crypto.state",
                "SELinux" to "getenforce 2>/dev/null || echo N/A",
            )
            info = cmds.map { (label, cmd) ->
                val r = ShellExecutor.execute(cmd)
                label to if (r.success && r.output.isNotBlank()) r.output.trim() else "N/A"
            }
            loading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("System Info") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        if (loading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(info.size) { idx ->
                    val (label, value) = info[idx]
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(label, modifier = Modifier.weight(1f), fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    }
}
