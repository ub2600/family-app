package com.familyapp.ui.screens.chat

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ChatMessage(val id: Long, val content: String, val senderName: String, val time: String, val isMe: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen() {
    var messageText by remember { mutableStateOf("") }
    val messages = remember {
        mutableStateListOf(
            ChatMessage(1, "Don't forget Tom has football at 4pm 🏈", "Mum", "09:15", false),
            ChatMessage(2, "Got it! I'll pick him up", "Dad", "09:22", false),
            ChatMessage(3, "Can we have pizza tonight? 🍕", "Tom", "10:45", false),
            ChatMessage(4, "How about pasta instead?", "Mum", "10:50", false),
            ChatMessage(5, "Pasta sounds great!", "Dad", "11:15", true),
            ChatMessage(6, "I finished all my homework! 🎉", "Emma", "14:20", false),
            ChatMessage(7, "Well done Emma! ⭐", "Dad", "14:25", true)
        )
    }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center) { Text("👨‍👩‍👧‍👦", fontSize = 20.sp) }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Family Chat", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("4 members", fontSize = 12.sp, color = Color(0xFF22C55E))
                    }
                }
            })
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(Modifier.weight(1f).padding(horizontal = 12.dp), state = listState,
                verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(vertical = 12.dp)) {
                items(messages) { msg ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = if (msg.isMe) Arrangement.End else Arrangement.Start) {
                        if (!msg.isMe) {
                            Box(Modifier.size(32.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(0.2f)),
                                contentAlignment = Alignment.Center) {
                                Text(msg.senderName.first().toString(), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                            Spacer(Modifier.width(8.dp))
                        }
                        Column(horizontalAlignment = if (msg.isMe) Alignment.End else Alignment.Start, modifier = Modifier.widthIn(max = 280.dp)) {
                            if (!msg.isMe) Text(msg.senderName, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                            Card(colors = CardDefaults.cardColors(containerColor = if (msg.isMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant),
                                shape = RoundedCornerShape(16.dp, 16.dp, if (msg.isMe) 4.dp else 16.dp, if (msg.isMe) 16.dp else 4.dp)) {
                                Column(Modifier.padding(12.dp)) {
                                    Text(msg.content, color = if (msg.isMe) Color.White else MaterialTheme.colorScheme.onSurface, fontSize = 15.sp)
                                    Text(msg.time, fontSize = 11.sp, color = (if (msg.isMe) Color.White else MaterialTheme.colorScheme.onSurface).copy(0.6f), modifier = Modifier.align(Alignment.End))
                                }
                            }
                        }
                    }
                }
            }
            Surface(Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.surface, shadowElevation = 4.dp) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = {}) { Icon(Icons.Filled.AttachFile, "Attach") }
                    TextField(messageText, { messageText = it }, Modifier.weight(1f), placeholder = { Text("Type a message...") },
                        shape = RoundedCornerShape(24.dp), singleLine = true,
                        colors = TextFieldDefaults.colors(unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant, focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedIndicatorColor = Color.Transparent, focusedIndicatorColor = Color.Transparent))
                    FilledIconButton(onClick = {
                        if (messageText.isNotBlank()) { messages.add(ChatMessage(messages.size + 1L, messageText, "Me", "Now", true)); messageText = "" }
                    }, colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primary)) {
                        Icon(Icons.Filled.Send, "Send")
                    }
                }
            }
        }
    }
}
