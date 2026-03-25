package com.familyapp.ui.screens.meals

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MealsViewModel @Inject constructor(private val repo: FamilyRepository) : ViewModel() {
    private val _meals = MutableStateFlow<List<MealPlanResponse>>(emptyList())
    val meals = _meals.asStateFlow()

    init { load() }

    fun load() { viewModelScope.launch { repo.getMealPlans().fold(onSuccess = { _meals.value = it }, onFailure = {}) } }

    fun addMeal(date: String, type: String, name: String, notes: String?, servings: Int) {
        viewModelScope.launch { repo.createMealPlan(MealPlanRequest(date, type, name, null, notes, servings)).fold(onSuccess = { load() }, onFailure = {}) }
    }

    fun deleteMeal(id: Long) { viewModelScope.launch { repo.deleteMealPlan(id).fold(onSuccess = { load() }, onFailure = {}) } }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealPlannerScreen(viewModel: MealsViewModel = hiltViewModel()) {
    var selectedDay by remember { mutableStateOf(0) }
    var showAddDialog by remember { mutableStateOf(false) }
    val meals by viewModel.meals.collectAsState()
    val weekDays = (0..6).map { LocalDate.now().plusDays(it.toLong()) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Meal Planner", fontWeight = FontWeight.Bold) }) },
        floatingActionButton = { FloatingActionButton(onClick = { showAddDialog = true }, containerColor = MaterialTheme.colorScheme.primary) { Icon(Icons.Filled.Add, "Add Meal") } }
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                LazyRow(Modifier.padding(horizontal = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(horizontal = 8.dp)) {
                    items(weekDays.size) { index ->
                        val date = weekDays[index]; val isSelected = index == selectedDay
                        Card(Modifier.width(64.dp).clickable { selectedDay = index }, colors = CardDefaults.cardColors(containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant), shape = RoundedCornerShape(14.dp)) {
                            Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()), fontSize = 12.sp, color = if (isSelected) Color.White.copy(0.7f) else MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${date.dayOfMonth}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface)
                                if (index == 0) Text("Today", fontSize = 10.sp, color = if (isSelected) Color.White.copy(0.7f) else MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
            item { Text("Meals for ${weekDays[selectedDay].dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())}", Modifier.padding(horizontal = 16.dp), fontWeight = FontWeight.SemiBold, fontSize = 16.sp) }

            val dayMeals = meals.filter { it.date == weekDays[selectedDay].toString() }
            if (dayMeals.isEmpty()) {
                item { Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), shape = RoundedCornerShape(12.dp)) {
                    Column(Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text("🍽️", fontSize = 48.sp); Spacer(Modifier.height(12.dp)); Text("No meals planned", fontWeight = FontWeight.Medium); Text("Tap + to add a meal", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                } }
            }
            items(dayMeals) { meal ->
                val icon = when (meal.mealType) { "Breakfast" -> "🥣"; "Lunch" -> "🥪"; "Dinner" -> "🍝"; "Snack" -> "🍎"; else -> "🍽️" }
                Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), shape = RoundedCornerShape(12.dp)) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(icon, fontSize = 36.sp); Spacer(Modifier.width(16.dp))
                        Column(Modifier.weight(1f)) {
                            Text(meal.mealType, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
                            Text(meal.recipeName, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                            Text("${meal.servings} servings", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = { viewModel.deleteMeal(meal.id) }) { Icon(Icons.Filled.Delete, "Delete", tint = MaterialTheme.colorScheme.error) }
                    }
                }
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }

    if (showAddDialog) {
        var recipeName by remember { mutableStateOf("") }; var notes by remember { mutableStateOf("") }
        val mealTypes = listOf("Breakfast", "Lunch", "Dinner", "Snack")
        var selectedType by remember { mutableStateOf("Dinner") }
        AlertDialog(onDismissRequest = { showAddDialog = false }, title = { Text("Add Meal", fontWeight = FontWeight.Bold) },
            text = { Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(recipeName, { recipeName = it }, Modifier.fillMaxWidth(), label = { Text("Recipe Name") }, singleLine = true)
                Text("Meal Type", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) { mealTypes.forEach { t -> FilterChip(selectedType == t, { selectedType = t }, { Text(t, fontSize = 12.sp) }) } }
                OutlinedTextField(notes, { notes = it }, Modifier.fillMaxWidth(), label = { Text("Notes (optional)") }, singleLine = true)
            } },
            confirmButton = { Button(onClick = { viewModel.addMeal(weekDays[selectedDay].toString(), selectedType, recipeName, notes.ifBlank { null }, 4); showAddDialog = false }, enabled = recipeName.isNotBlank()) { Text("Add") } },
            dismissButton = { TextButton(onClick = { showAddDialog = false }) { Text("Cancel") } })
    }
}
