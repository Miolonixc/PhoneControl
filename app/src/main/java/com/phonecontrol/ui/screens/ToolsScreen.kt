package com.phonecontrol.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.phonecontrol.util.ShellExecutor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolsScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var output by remember { mutableStateOf("Ready") }

    fun run(cmd: String) {
        scope.launch {
            output = "Running..."
            val r = ShellExecutor.execute(cmd)
            output = if (r.success) r.output.ifBlank { "Done" } else r.error.ifBlank { "Error" }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Tools") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } })
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Section("Screenshot")
            ActionRow("Take Screenshot", Icons.Default.CameraAlt) {
                run("screencap -p /sdcard/Download/screenshot_\$(date +%Y%m%d_%H%M%S).png")
            }

            Section("Screen")
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ActionBtn(Modifier.weight(1f), "Screen Off", Icons.Default.BrightnessLow) { run("input keyevent 26") }
                ActionBtn(Modifier.weight(1f), "Screen On", Icons.Default.BrightnessHigh) { run("input keyevent 224") }
            }

            Section("Navigation Keys")
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ActionBtn(Modifier.weight(1f), "Home", Icons.Default.Home) { run("input keyevent 3") }
                ActionBtn(Modifier.weight(1f), "Back", Icons.Default.ArrowBack) { run("input keyevent 4") }
                ActionBtn(Modifier.weight(1f), "Recent", Icons.Default.GridView) { run("input keyevent 187") }
            }

            Section("Volume")
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ActionBtn(Modifier.weight(1f), "Vol +", Icons.Default.VolumeUp) { run("input keyevent 24") }
                ActionBtn(Modifier.weight(1f), "Vol -", Icons.Default.VolumeDown) { run("input keyevent 25") }
                ActionBtn(Modifier.weight(1f), "Mute", Icons.Default.VolumeOff) { run("input keyevent 164") }
            }

            Section("Display")
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ActionBtn(Modifier.weight(1f), "Bright Max", Icons.Default.BrightnessHigh) { run("cmd display set-brightness 0 1.0") }
                ActionBtn(Modifier.weight(1f), "Bright 50%", Icons.Default.BrightnessMedium) { run("cmd display set-brightness 0 0.5") }
                ActionBtn(Modifier.weight(1f), "Bright Min", Icons.Default.BrightnessLow) { run("cmd display set-brightness 0 0.1") }
            }

            Section("Auto Rotate")
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ActionBtn(Modifier.weight(1f), "Enable", Icons.Default.ScreenRotation) { run("settings put system accelerometer_rotation 1") }
                ActionBtn(Modifier.weight(1f), "Disable", Icons.Default.ScreenLockRotation) { run("settings put system accelerometer_rotation 0") }
            }

            Section("Output")
            Card(
                Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Text(output, modifier = Modifier.padding(12.dp), fontSize = 12.sp, lineHeight = 16.sp)
            }
        }
    }
}

@Composable
private fun Section(title: String) {
    Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp))
}

@Composable
private fun ActionRow(label: String, icon: ImageVector, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Icon(icon, null, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text(label)
    }
}

@Composable
private fun ActionBtn(modifier: Modifier, label: String, icon: ImageVector, onClick: () -> Unit) {
    OutlinedButton(onClick = onClick, modifier = modifier, shape = RoundedCornerShape(12.dp)) {
        Icon(icon, null, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(4.dp))
        Text(label, fontSize = 12.sp)
    }
}
