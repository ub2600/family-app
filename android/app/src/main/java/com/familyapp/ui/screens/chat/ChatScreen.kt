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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.familyapp.data.model.*
import com.familyapp.data.repository.FamilyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(private val repo: FamilyRepository) : ViewModel() {
    private val _messages = MutableStateFlow<List<MessageResponse>>(emptyList())
    val messages = _messages.asStateFlow()
    private val _userId = MutableStateFlow(0L)
    val userId = _userId.asStateFlow()

    init { load(); viewModelScope.launch { _userId.value = repo.getUserId() }
        // Poll for new messages every 5 seconds
        viewModelScope.launch { while (true) { delay(5000); load() } }
    }

    fun load() { viewModelScope.launch { repo.getMessages().fold(onSuccess = { _messages.value = it }, onFailure = {}) } }

    fun send(content: String) {
        viewModelScope.launch { repo.sendMessage(SendMessageRequest(content)).fold(onSuccess = { load() }, onFailure = {}) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(viewModel: ChatViewModel = hiltViewModel()) {
    var messageText by remember { mutableStateOf("") }
    val messages by viewModel.messages.collectAsState()
    val userId by viewModel.userId.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary), contentAlignment = Alignment.Center) { Text("👨‍👩‍👧‍👦", fontSize = 20.sp) }
                    Spacer(Modifier.width(12.dp))
                    Column { Text("Family Chat", fontWeight = FontWeight.Bold, fontSize = 16.sp); Text("Online", fontSize = 12.sp, color = Color(0xFF22C55E)) }
                }
            })
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            if (messages.isEmpty()) {
                Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("💬", fontSize = 48.sp); Spacer(Modifier.height(8.dp)); Text("No messages yet"); Text("Start the conversation!", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                }
            } else {
                LazyColumn(Modifier.weight(1f).padding(horizontal = 12.dp), state = listState, verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(vertical = 12.dp)) {
                    items(messages) { msg ->
                        val isMe = msg.senderId == userId
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start) {
                            if (!isMe) {
                                Box(Modifier.size(32.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(0.2f)), contentAlignment = Alignment.Center) {
                                    Text(msg.senderName.first().toString(), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                }
                                Spacer(Modifier.width(8.dp))
                            }
                            Column(horizontalAlignment = if (isMe) Alignment.End else Alignment.Start, modifier = Modifier.widthIn(max = 280.dp)) {
                                if (!isMe) Text(msg.senderName, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                                Card(colors = CardDefaults.cardColors(containerColor = if (isMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant),
                                    shape = RoundedCornerShape(16.dp, 16.dp, if (isMe) 4.dp else 16.dp, if (isMe) 16.dp else 4.dp)) {
                                    Column(Modifier.padding(12.dp)) {
                                        Text(msg.content ?: "", color = if (isMe) Color.White else MaterialTheme.colorScheme.onSurface, fontSize = 15.sp)
                                        Text(msg.timestamp, fontSize = 11.sp, color = (if (isMe) Color.White else MaterialTheme.colorScheme.onSurface).copy(0.6f), modifier = Modifier.align(Alignment.End))
                                    }
                                }
                            }
                        }
                    }
                }
            }
            // Input bar
            Surface(Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.surface, shadowElevation = 4.dp) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextField(messageText, { messageText = it }, Modifier.weight(1f), placeholder = { Text("Type a message...") },
                        shape = RoundedCornerShape(24.dp), singleLine = true,
                        colors = TextFieldDefaults.colors(unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant, focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedIndicatorColor = Color.Transparent, focusedIndicatorColor = Color.Transparent))
                    FilledIconButton(onClick = { if (messageText.isNotBlank()) { viewModel.send(messageText); messageText = "" } },
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primary)) { Icon(Icons.Filled.Send, "Send") }
                }
            }
        }
    }
}
