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

data class TodoListUI(val id: Long, val name: String, val icon: String, val total: Int, val completed: Int)
data class ListItemUI(val id: Long, val text: String, val completed: Boolean, val quantity: String?)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListsScreen() {
    var selectedList by remember { mutableStateOf<TodoListUI?>(null) }
    var showAddListDialog by remember { mutableStateOf(false) }
    var showAddItemDialog by remember { mutableStateOf(false) }

    val lists = remember {
        mutableStateListOf(
            TodoListUI(1, "Grocery Shopping", "🛒", 8, 3),
            TodoListUI(2, "Weekend Chores", "🧹", 5, 1),
            TodoListUI(3, "School Supplies", "📚", 6, 6),
            TodoListUI(4, "Holiday Packing", "🧳", 12, 0)
        )
    }

    val items = remember {
        mutableStateListOf(
            ListItemUI(1, "Milk (Semi-skimmed)", false, "2L"),
            ListItemUI(2, "Bread (Wholemeal)", false, "1"),
            ListItemUI(3, "Chicken Breasts", true, "500g"),
            ListItemUI(4, "Broccoli", false, "2"),
            ListItemUI(5, "Rice", true, "1kg"),
            ListItemUI(6, "Olive Oil", false, null),
            ListItemUI(7, "Pasta", true, "500g"),
            ListItemUI(8, "Tomato Sauce", false, "2")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (selectedList != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(selectedList!!.icon, fontSize = 24.sp)
                            Spacer(Modifier.width(8.dp))
                            Text(selectedList!!.name, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Text("Lists & To-Do", fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    if (selectedList != null) {
                        IconButton(onClick = { selectedList = null }) {
                            Icon(Icons.Filled.ArrowBack, "Back")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedList != null) showAddItemDialog = true
                    else showAddListDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Filled.Add, "Add")
            }
        }
    ) { padding ->
        if (selectedList == null) {
            // Lists view
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(lists) { list ->
                    ListCard(list = list, onClick = { selectedList = list })
                }
            }
        } else {
            // List items view
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                // Progress
                item {
                    val completed = items.count { it.completed }
                    val total = items.size
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Progress", fontWeight = FontWeight.SemiBold)
                                Text("$completed/$total", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { if (total > 0) completed.toFloat() / total else 0f },
                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            )
                        }
                    }
                }

                items(items) { item ->
                    ListItemCard(
                        item = item,
                        onToggle = {
                            val idx = items.indexOfFirst { it.id == item.id }
                            if (idx >= 0) items[idx] = items[idx].copy(completed = !items[idx].completed)
                        }
                    )
                }

                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun ListCard(list: TodoListUI, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(list.icon, fontSize = 32.sp)
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(list.name, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Text(
                    "${list.completed}/${list.total} completed",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            CircularProgressIndicator(
                progress = { if (list.total > 0) list.completed.toFloat() / list.total else 0f },
                modifier = Modifier.size(40.dp),
                strokeWidth = 4.dp,
                color = if (list.completed == list.total) Color(0xFF22C55E) else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
fun ListItemCard(item: ListItemUI, onToggle: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (item.completed) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = item.completed,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF22C55E))
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.text,
                    fontSize = 15.sp,
                    textDecoration = if (item.completed) TextDecoration.LineThrough else null,
                    color = if (item.completed) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                )
            }
            item.quantity?.let {
                Text(
                    it,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
    }
}
