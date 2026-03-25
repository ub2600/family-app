package com.familyapp.ui.screens.noticeboard

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoticeBoardViewModel @Inject constructor(private val repo: FamilyRepository) : ViewModel() {
    private val _notices = MutableStateFlow<List<NoticeBoardResponse>>(emptyList())
    val notices = _notices.asStateFlow()

    init { load() }

    fun load() { viewModelScope.launch { repo.getNotices().fold(onSuccess = { _notices.value = it }, onFailure = {}) } }

    fun addNotice(title: String, content: String, color: String, pinned: Boolean) {
        viewModelScope.launch { repo.createNotice(NoticeBoardRequest(title, content, pinned, color)).fold(onSuccess = { load() }, onFailure = {}) }
    }

    fun togglePin(id: Long) { viewModelScope.launch { repo.togglePin(id).fold(onSuccess = { load() }, onFailure = {}) } }

    fun delete(id: Long) { viewModelScope.launch { repo.deleteNotice(id).fold(onSuccess = { load() }, onFailure = {}) } }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoticeBoardScreen(onNavigateBack: () -> Unit, viewModel: NoticeBoardViewModel = hiltViewModel()) {
    val notices by viewModel.notices.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Notice Board", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Filled.ArrowBack, "Back") } }) },
        floatingActionButton = { FloatingActionButton(onClick = { showAddDialog = true }, containerColor = MaterialTheme.colorScheme.primary) { Icon(Icons.Filled.Add, "Add Notice") } }
    ) { padding ->
        if (notices.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("📌", fontSize = 48.sp); Spacer(Modifier.height(8.dp)); Text("No notices yet"); Text("Tap + to post one", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(vertical = 8.dp)) {
                val pinned = notices.filter { it.pinned }; val unpinned = notices.filter { !it.pinned }
                if (pinned.isNotEmpty()) { item { Text("📌 Pinned", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) } }
                items(pinned) { notice -> NoticeCard(notice, viewModel) }
                if (unpinned.isNotEmpty()) { item { Text("Recent", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) } }
                items(unpinned) { notice -> NoticeCard(notice, viewModel) }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }

    if (showAddDialog) {
        var title by remember { mutableStateOf("") }; var content by remember { mutableStateOf("") }; var pinned by remember { mutableStateOf(false) }
        val colors = listOf("#FFD700", "#7C5CFC", "#22C55E", "#F97316", "#EC4899", "#3B82F6")
        var selectedColor by remember { mutableStateOf(colors[0]) }
        AlertDialog(onDismissRequest = { showAddDialog = false }, title = { Text("Post Notice", fontWeight = FontWeight.Bold) },
            text = { Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(title, { title = it }, Modifier.fillMaxWidth(), label = { Text("Title") }, singleLine = true)
                OutlinedTextField(content, { content = it }, Modifier.fillMaxWidth(), label = { Text("Content") }, minLines = 3, maxLines = 5)
                Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(pinned, { pinned = it }); Text("Pin this notice") }
                Text("Colour", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    colors.forEach { c -> Box(Modifier.size(32.dp).clip(CircleShape).background(Color(android.graphics.Color.parseColor(c)))
                        .then(if (c == selectedColor) Modifier.border(2.dp, Color.White, CircleShape) else Modifier).clickable { selectedColor = c }) }
                }
            } },
            confirmButton = { Button(onClick = { viewModel.addNotice(title, content, selectedColor, pinned); showAddDialog = false }, enabled = title.isNotBlank() && content.isNotBlank()) { Text("Post") } },
            dismissButton = { TextButton(onClick = { showAddDialog = false }) { Text("Cancel") } })
    }
}

@Composable
fun NoticeCard(notice: NoticeBoardResponse, viewModel: NoticeBoardViewModel) {
    val color = try { Color(android.graphics.Color.parseColor(notice.color)) } catch (e: Exception) { Color(0xFFFFD700) }
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), shape = RoundedCornerShape(14.dp)) {
        Column {
            Box(Modifier.fillMaxWidth().height(4.dp).background(color))
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(notice.title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, modifier = Modifier.weight(1f))
                    IconButton(onClick = { viewModel.togglePin(notice.id) }) { Icon(Icons.Filled.PushPin, null, tint = if (notice.pinned) Color(0xFFFFD700) else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp)) }
                    IconButton(onClick = { viewModel.delete(notice.id) }) { Icon(Icons.Filled.Delete, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp)) }
                }
                Text(notice.content, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 20.sp)
                Spacer(Modifier.height(8.dp))
                Text("${notice.createdByName ?: ""} • ${notice.createdAt}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f))
            }
        }
    }
}
