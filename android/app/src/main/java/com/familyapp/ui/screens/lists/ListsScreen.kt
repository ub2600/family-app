package com.familyapp.ui.screens.lists

import androidx.compose.foundation.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
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
class ListsViewModel @Inject constructor(private val repo: FamilyRepository) : ViewModel() {
    private val _lists = MutableStateFlow<List<TodoListResponse>>(emptyList())
    val lists = _lists.asStateFlow()
    private val _items = MutableStateFlow<List<ListItemResponse>>(emptyList())
    val items = _items.asStateFlow()
    private val _selectedList = MutableStateFlow<TodoListResponse?>(null)
    val selectedList = _selectedList.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init { loadLists() }

    fun loadLists() { viewModelScope.launch { _isLoading.value = true; repo.getLists().fold(onSuccess = { _lists.value = it }, onFailure = {}); _isLoading.value = false } }

    fun createList(name: String, icon: String) { viewModelScope.launch { repo.createList(CreateListRequest(name, icon)).fold(onSuccess = { loadLists() }, onFailure = {}) } }

    fun selectList(list: TodoListResponse?) {
        _selectedList.value = list
        if (list != null) loadItems(list.id)
    }

    fun loadItems(listId: Long) { viewModelScope.launch { repo.getListItems(listId).fold(onSuccess = { _items.value = it }, onFailure = {}) } }

    fun addItem(text: String, quantity: String?) {
        val list = _selectedList.value ?: return
        viewModelScope.launch { repo.addListItem(list.id, ListItemRequest(text, quantity?.toIntOrNull())).fold(onSuccess = { loadItems(list.id); loadLists() }, onFailure = {}) }
    }

    fun toggleItem(itemId: Long) {
        val list = _selectedList.value ?: return
        viewModelScope.launch { repo.toggleListItem(itemId).fold(onSuccess = { loadItems(list.id); loadLists() }, onFailure = {}) }
    }

    fun deleteItem(itemId: Long) {
        val list = _selectedList.value ?: return
        viewModelScope.launch { repo.deleteListItem(itemId).fold(onSuccess = { loadItems(list.id); loadLists() }, onFailure = {}) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListsScreen(viewModel: ListsViewModel = hiltViewModel()) {
    val lists by viewModel.lists.collectAsState()
    val items by viewModel.items.collectAsState()
    val selectedList by viewModel.selectedList.collectAsState()
    var showAddListDialog by remember { mutableStateOf(false) }
    var showAddItemDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (selectedList != null) Row(verticalAlignment = Alignment.CenterVertically) { Text(selectedList!!.icon, fontSize = 24.sp); Spacer(Modifier.width(8.dp)); Text(selectedList!!.name, fontWeight = FontWeight.Bold) }
                    else Text("Lists & To-Do", fontWeight = FontWeight.Bold)
                },
                navigationIcon = { if (selectedList != null) IconButton(onClick = { viewModel.selectList(null) }) { Icon(Icons.Filled.ArrowBack, "Back") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { if (selectedList != null) showAddItemDialog = true else showAddListDialog = true }, containerColor = MaterialTheme.colorScheme.primary) { Icon(Icons.Filled.Add, "Add") }
        }
    ) { padding ->
        if (selectedList == null) {
            if (lists.isEmpty()) {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("📋", fontSize = 48.sp); Spacer(Modifier.height(8.dp)); Text("No lists yet"); Text("Tap + to create one", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                }
            } else {
                LazyColumn(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(vertical = 8.dp)) {
                    items(lists) { list ->
                        Card(Modifier.fillMaxWidth().clickable { viewModel.selectList(list) }, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), shape = RoundedCornerShape(12.dp)) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(list.icon, fontSize = 32.sp); Spacer(Modifier.width(16.dp))
                                Column(Modifier.weight(1f)) { Text(list.name, fontWeight = FontWeight.SemiBold, fontSize = 16.sp); Text("${list.completedCount}/${list.itemCount} completed", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                                CircularProgressIndicator(progress = if (list.itemCount > 0) list.completedCount.toFloat() / list.itemCount else 0f, Modifier.size(40.dp), strokeWidth = 4.dp,
                                    color = if (list.completedCount == list.itemCount && list.itemCount > 0) Color(0xFF22C55E) else MaterialTheme.colorScheme.primary, trackColor = MaterialTheme.colorScheme.outline.copy(0.3f))
                            }
                        }
                    }
                }
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(vertical = 8.dp)) {
                item {
                    val completed = items.count { it.completed }; val total = items.size
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(0.1f)), shape = RoundedCornerShape(12.dp)) {
                        Column(Modifier.padding(16.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Progress", fontWeight = FontWeight.SemiBold); Text("$completed/$total", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold) }
                            Spacer(Modifier.height(8.dp))
                            LinearProgressIndicator(progress = if (total > 0) completed.toFloat() / total else 0f, Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)), color = MaterialTheme.colorScheme.primary, trackColor = MaterialTheme.colorScheme.primary.copy(0.15f))
                        }
                    }
                }
                if (items.isEmpty()) {
                    item { Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), shape = RoundedCornerShape(12.dp)) {
                        Column(Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text("📝", fontSize = 48.sp); Spacer(Modifier.height(8.dp)); Text("No items yet"); Text("Tap + to add items", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                    } }
                }
                items(items) { item ->
                    Card(colors = CardDefaults.cardColors(containerColor = if (item.completed) MaterialTheme.colorScheme.surfaceVariant.copy(0.5f) else MaterialTheme.colorScheme.surfaceVariant), shape = RoundedCornerShape(10.dp)) {
                        Row(Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(item.completed, { viewModel.toggleItem(item.id) }, colors = CheckboxDefaults.colors(checkedColor = Color(0xFF22C55E)))
                            Text(item.text, Modifier.weight(1f), fontSize = 15.sp, textDecoration = if (item.completed) TextDecoration.LineThrough else null,
                                color = if (item.completed) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface)
                            item.quantity?.let { Text("$it", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium,
                                modifier = Modifier.background(MaterialTheme.colorScheme.primary.copy(0.1f), RoundedCornerShape(6.dp)).padding(horizontal = 8.dp, vertical = 2.dp)) }
                            IconButton(onClick = { viewModel.deleteItem(item.id) }) { Icon(Icons.Filled.Close, "Delete", Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant) }
                        }
                    }
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }

    if (showAddListDialog) {
        var name by remember { mutableStateOf("") }; var icon by remember { mutableStateOf("📋") }
        val icons = listOf("📋", "🛒", "🧹", "📚", "🧳", "🎁", "💊", "🏠")
        AlertDialog(onDismissRequest = { showAddListDialog = false }, title = { Text("Create List", fontWeight = FontWeight.Bold) },
            text = { Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(name, { name = it }, Modifier.fillMaxWidth(), label = { Text("List Name") }, singleLine = true)
                Text("Icon", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { icons.forEach { i -> Box(Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(if (i == icon) MaterialTheme.colorScheme.primary.copy(0.2f) else Color.Transparent).clickable { icon = i }, contentAlignment = Alignment.Center) { Text(i, fontSize = 24.sp) } } }
            } },
            confirmButton = { Button(onClick = { viewModel.createList(name, icon); showAddListDialog = false }, enabled = name.isNotBlank()) { Text("Create") } },
            dismissButton = { TextButton(onClick = { showAddListDialog = false }) { Text("Cancel") } })
    }

    if (showAddItemDialog) {
        var text by remember { mutableStateOf("") }; var quantity by remember { mutableStateOf("") }
        AlertDialog(onDismissRequest = { showAddItemDialog = false }, title = { Text("Add Item", fontWeight = FontWeight.Bold) },
            text = { Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(text, { text = it }, Modifier.fillMaxWidth(), label = { Text("Item name") }, singleLine = true)
                OutlinedTextField(quantity, { quantity = it }, Modifier.fillMaxWidth(), label = { Text("Quantity (optional)") }, singleLine = true)
            } },
            confirmButton = { Button(onClick = { viewModel.addItem(text, quantity.ifBlank { null }); showAddItemDialog = false }, enabled = text.isNotBlank()) { Text("Add") } },
            dismissButton = { TextButton(onClick = { showAddItemDialog = false }) { Text("Cancel") } })
    }
}
