package com.phonecontrol.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
fun TerminalScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var input by remember { mutableStateOf("") }
    var history by remember { mutableStateOf(listOf<Pair<String, String>>()) }
    val listState = rememberLazyListState()

    fun run() {
        if (input.isBlank()) return
        val cmd = input; input = ""
        scope.launch {
            val r = ShellExecutor.execute(cmd)
            val out = if (r.success) r.output.ifBlank { "(no output)" } else "Error: ${r.error.ifBlank { "exit ${r.exitCode}" }}"
            history = history + (cmd to out)
            kotlinx.coroutines.delay(100)
            listState.animateScrollToItem(history.size)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Terminal", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.Send, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E1E1E), titleContentColor = Color(0xFF00E676)))
        },
        containerColor = Color(0xFF121212)
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(history) { (cmd, out) ->
                    Column(Modifier.clip(RoundedCornerShape(8.dp)).background(Color(0xFF1E1E1E)).padding(10.dp)) {
                        Text("$ $cmd", fontFamily = FontFamily.Monospace, fontSize = 13.sp, color = Color(0xFF00E676), fontWeight = FontWeight.Bold)
                        Text(out, fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = Color(0xFFB0BEC5), lineHeight = 16.sp)
                    }
                }
            }

            Row(Modifier.padding(8.dp).clip(RoundedCornerShape(14.dp)).background(Color(0xFF1E1E1E)).padding(4.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("$ ", fontFamily = FontFamily.Monospace, color = Color(0xFF546E7A)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color(0xFF00E676), unfocusedTextColor = Color(0xFFB0BEC5), cursorColor = Color(0xFF00E676), focusedBorderColor = Color(0xFF00E676), unfocusedBorderColor = Color(0xFF37474F)),
                    textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace, fontSize = 14.sp)
                )
                IconButton(onClick = { run() }, enabled = input.isNotBlank()) {
                    Icon(Icons.Default.Send, null, tint = if (input.isNotBlank()) Color(0xFF00E676) else Color(0xFF37474F))
                }
            }
        }
    }
}
