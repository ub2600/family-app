package com.familyapp.ui.screens.expenses

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ExpenseUI(val id: Long, val description: String, val amount: Double, val category: String, val date: String, val paidBy: String)
data class SavingsGoalUI(val id: Long, val name: String, val icon: String, val target: Double, val current: Double, val percent: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesScreen() {
    val expenses = remember {
        listOf(
            ExpenseUI(1, "Tesco Weekly Shop", 87.50, "Groceries", "24 Mar", "Mum"),
            ExpenseUI(2, "Electric Bill", 142.00, "Bills", "22 Mar", "Dad"),
            ExpenseUI(3, "Kids' School Lunch", 35.00, "Food", "21 Mar", "Mum"),
            ExpenseUI(4, "Petrol", 65.00, "Transport", "20 Mar", "Dad"),
            ExpenseUI(5, "Swimming Lessons", 48.00, "Activities", "19 Mar", "Dad")
        )
    }

    val savingsGoals = remember {
        listOf(
            SavingsGoalUI(1, "Holiday Fund", "✈️", 3000.0, 1850.0, 62),
            SavingsGoalUI(2, "New Sofa", "🛋️", 1200.0, 450.0, 38)
        )
    }

    val totalThisMonth = expenses.sumOf { it.amount }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Family Finances", fontWeight = FontWeight.Bold) })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { },
                containerColor = MaterialTheme.colorScheme.primary
            ) { Icon(Icons.Filled.Add, "Add Expense") }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            // Summary Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(listOf(Color(0xFF7C5CFC), Color(0xFFA855F7))),
                                RoundedCornerShape(16.dp)
                            )
                            .padding(24.dp)
                    ) {
                        Column {
                            Text("This Month", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                            Text(
                                "£${String.format("%.2f", totalThisMonth)}",
                                color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Column {
                                    Text("Transactions", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                                    Text("${expenses.size}", color = Color.White, fontWeight = FontWeight.SemiBold)
                                }
                                Column {
                                    Text("Avg per day", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                                    Text("£${String.format("%.2f", totalThisMonth / 24)}", color = Color.White, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            }

            // Savings Goals
            item {
                Text("Savings Goals", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }

            items(savingsGoals) { goal ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(goal.icon, fontSize = 32.sp)
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(goal.name, fontWeight = FontWeight.SemiBold)
                            Text(
                                "£${String.format("%.0f", goal.current)} / £${String.format("%.0f", goal.target)}",
                                fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { goal.percent / 100f },
                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                color = Color(0xFF22C55E),
                                trackColor = Color(0xFF22C55E).copy(alpha = 0.15f)
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Text("${goal.percent}%", fontWeight = FontWeight.Bold, color = Color(0xFF22C55E))
                    }
                }
            }

            // Recent Expenses
            item {
                Spacer(Modifier.height(4.dp))
                Text("Recent Expenses", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }

            items(expenses) { expense ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val categoryIcon = when (expense.category) {
                            "Groceries" -> "🛒"
                            "Bills" -> "💡"
                            "Food" -> "🍽️"
                            "Transport" -> "⛽"
                            "Activities" -> "🏊"
                            else -> "💰"
                        }
                        Text(categoryIcon, fontSize = 28.sp)
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(expense.description, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                            Text(
                                "${expense.date} • ${expense.paidBy}",
                                fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            "-£${String.format("%.2f", expense.amount)}",
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFF85149)
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}
