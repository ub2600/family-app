package com.familyapp.ui.screens.expenses

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
class ExpensesViewModel @Inject constructor(private val repo: FamilyRepository) : ViewModel() {
    private val _expenses = MutableStateFlow<List<ExpenseResponse>>(emptyList())
    val expenses = _expenses.asStateFlow()
    private val _savings = MutableStateFlow<List<SavingsGoalResponse>>(emptyList())
    val savings = _savings.asStateFlow()

    init { load() }

    fun load() { viewModelScope.launch { repo.getExpenses().fold(onSuccess = { _expenses.value = it }, onFailure = {}); repo.getSavingsGoals().fold(onSuccess = { _savings.value = it }, onFailure = {}) } }

    fun addExpense(amount: Double, description: String, category: String) { viewModelScope.launch { repo.addExpense(ExpenseRequest(amount, description, category)).fold(onSuccess = { load() }, onFailure = {}) } }

    fun deleteExpense(id: Long) { viewModelScope.launch { repo.deleteExpense(id).fold(onSuccess = { load() }, onFailure = {}) } }

    fun addSavingsGoal(name: String, target: Double, icon: String) { viewModelScope.launch { repo.createSavingsGoal(SavingsGoalRequest(name, target, icon)).fold(onSuccess = { load() }, onFailure = {}) } }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesScreen(viewModel: ExpensesViewModel = hiltViewModel()) {
    val expenses by viewModel.expenses.collectAsState()
    val savings by viewModel.savings.collectAsState()
    var showAddExpense by remember { mutableStateOf(false) }
    var showAddSavings by remember { mutableStateOf(false) }
    val totalThisMonth = expenses.sumOf { it.amount }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Family Finances", fontWeight = FontWeight.Bold) }) },
        floatingActionButton = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SmallFloatingActionButton(onClick = { showAddSavings = true }, containerColor = Color(0xFF22C55E)) { Icon(Icons.Filled.Savings, "Savings Goal") }
                FloatingActionButton(onClick = { showAddExpense = true }, containerColor = MaterialTheme.colorScheme.primary) { Icon(Icons.Filled.Add, "Add Expense") }
            }
        }
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(vertical = 8.dp)) {
            item {
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.Transparent)) {
                    Box(Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(Color(0xFF7C5CFC), Color(0xFFA855F7))), RoundedCornerShape(16.dp)).padding(24.dp)) {
                        Column {
                            Text("Total Spent", color = Color.White.copy(0.7f), fontSize = 14.sp)
                            Text("£${String.format("%.2f", totalThisMonth)}", color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(8.dp))
                            Text("${expenses.size} transactions", color = Color.White.copy(0.7f), fontSize = 13.sp)
                        }
                    }
                }
            }
            if (savings.isNotEmpty()) {
                item { Text("Savings Goals", fontWeight = FontWeight.SemiBold, fontSize = 16.sp) }
                items(savings) { goal ->
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), shape = RoundedCornerShape(12.dp)) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(goal.icon, fontSize = 32.sp); Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(goal.name, fontWeight = FontWeight.SemiBold)
                                Text("£${String.format("%.0f", goal.currentAmount)} / £${String.format("%.0f", goal.targetAmount)}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(Modifier.height(6.dp))
                                LinearProgressIndicator(progress = goal.progressPercent / 100f, Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)), color = Color(0xFF22C55E), trackColor = Color(0xFF22C55E).copy(0.15f))
                            }
                            Spacer(Modifier.width(12.dp)); Text("${goal.progressPercent}%", fontWeight = FontWeight.Bold, color = Color(0xFF22C55E))
                        }
                    }
                }
            }
            item { Text("Recent Expenses", fontWeight = FontWeight.SemiBold, fontSize = 16.sp) }
            if (expenses.isEmpty()) {
                item { Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), shape = RoundedCornerShape(12.dp)) {
                    Column(Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text("💰", fontSize = 48.sp); Spacer(Modifier.height(8.dp)); Text("No expenses yet"); Text("Tap + to add one", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                } }
            }
            items(expenses) { expense ->
                val icon = when (expense.category) { "Groceries" -> "🛒"; "Bills" -> "💡"; "Food" -> "🍽️"; "Transport" -> "⛽"; "Activities" -> "🏊"; "Shopping" -> "🛍️"; else -> "💰" }
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), shape = RoundedCornerShape(10.dp)) {
                    Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(icon, fontSize = 28.sp); Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) { Text(expense.description, fontWeight = FontWeight.Medium, fontSize = 14.sp); Text("${expense.date} • ${expense.paidByName ?: ""}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        Text("-£${String.format("%.2f", expense.amount)}", fontWeight = FontWeight.SemiBold, color = Color(0xFFF85149))
                        IconButton(onClick = { viewModel.deleteExpense(expense.id) }) { Icon(Icons.Filled.Close, "Delete", Modifier.size(16.dp)) }
                    }
                }
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }

    if (showAddExpense) {
        var amount by remember { mutableStateOf("") }; var desc by remember { mutableStateOf("") }
        val categories = listOf("Groceries", "Bills", "Food", "Transport", "Activities", "Shopping", "General")
        var category by remember { mutableStateOf("General") }
        AlertDialog(onDismissRequest = { showAddExpense = false }, title = { Text("Add Expense", fontWeight = FontWeight.Bold) },
            text = { Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(amount, { amount = it }, Modifier.fillMaxWidth(), label = { Text("Amount (£)") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                OutlinedTextField(desc, { desc = it }, Modifier.fillMaxWidth(), label = { Text("Description") }, singleLine = true)
                Text("Category", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) { categories.forEach { c -> FilterChip(category == c, { category = c }, { Text(c, fontSize = 12.sp) }) } }
            } },
            confirmButton = { Button(onClick = { amount.toDoubleOrNull()?.let { viewModel.addExpense(it, desc, category) }; showAddExpense = false }, enabled = amount.isNotBlank() && desc.isNotBlank()) { Text("Add") } },
            dismissButton = { TextButton(onClick = { showAddExpense = false }) { Text("Cancel") } })
    }

    if (showAddSavings) {
        var name by remember { mutableStateOf("") }; var target by remember { mutableStateOf("") }; var icon by remember { mutableStateOf("🎯") }
        AlertDialog(onDismissRequest = { showAddSavings = false }, title = { Text("New Savings Goal", fontWeight = FontWeight.Bold) },
            text = { Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(name, { name = it }, Modifier.fillMaxWidth(), label = { Text("Goal Name") }, singleLine = true)
                OutlinedTextField(target, { target = it }, Modifier.fillMaxWidth(), label = { Text("Target Amount (£)") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { listOf("🎯", "✈️", "🛋️", "🚗", "🏠", "🎮").forEach { i -> Box(Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(if (i == icon) MaterialTheme.colorScheme.primary.copy(0.2f) else Color.Transparent).clickable { icon = i }, contentAlignment = Alignment.Center) { Text(i, fontSize = 24.sp) } } }
            } },
            confirmButton = { Button(onClick = { target.toDoubleOrNull()?.let { viewModel.addSavingsGoal(name, it, icon) }; showAddSavings = false }, enabled = name.isNotBlank() && target.isNotBlank()) { Text("Create") } },
            dismissButton = { TextButton(onClick = { showAddSavings = false }) { Text("Cancel") } })
    }
}
