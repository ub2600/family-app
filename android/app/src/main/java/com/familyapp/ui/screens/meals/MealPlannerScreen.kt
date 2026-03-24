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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

data class MealUI(val type: String, val icon: String, val name: String, val servings: Int)
data class DayMealPlan(val date: LocalDate, val meals: List<MealUI>)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealPlannerScreen() {
    var selectedDay by remember { mutableStateOf(0) }

    val weekDays = (0..6).map { LocalDate.now().plusDays(it.toLong()) }

    val mealPlans = remember {
        listOf(
            DayMealPlan(weekDays[0], listOf(
                MealUI("Breakfast", "🥣", "Porridge with Berries", 4),
                MealUI("Lunch", "🥪", "Chicken Caesar Wraps", 4),
                MealUI("Dinner", "🍝", "Spaghetti Bolognese", 4)
            )),
            DayMealPlan(weekDays[1], listOf(
                MealUI("Breakfast", "🍳", "Scrambled Eggs on Toast", 4),
                MealUI("Lunch", "🥗", "Greek Salad", 4),
                MealUI("Dinner", "🍛", "Chicken Curry & Rice", 4)
            )),
            DayMealPlan(weekDays[2], listOf(
                MealUI("Breakfast", "🥞", "Pancakes with Maple Syrup", 4),
                MealUI("Dinner", "🍕", "Homemade Pizza Night", 4)
            ))
        )
    }

    val currentPlan = mealPlans.getOrNull(selectedDay)

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Meal Planner", fontWeight = FontWeight.Bold) })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { },
                containerColor = MaterialTheme.colorScheme.primary
            ) { Icon(Icons.Filled.Add, "Add Meal") }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Week Day Selector
            item {
                LazyRow(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    items(weekDays.size) { index ->
                        val date = weekDays[index]
                        val isSelected = index == selectedDay
                        val isToday = index == 0

                        Card(
                            modifier = Modifier
                                .width(64.dp)
                                .clickable { selectedDay = index },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                                    fontSize = 12.sp,
                                    color = if (isSelected) Color.White.copy(alpha = 0.7f)
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    "${date.dayOfMonth}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                                if (isToday) {
                                    Text("Today", fontSize = 10.sp,
                                        color = if (isSelected) Color.White.copy(alpha = 0.7f) else MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
            }

            // Meals for selected day
            item {
                Spacer(Modifier.height(4.dp))
                Text(
                    "Meals for ${weekDays[selectedDay].dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())}",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }

            if (currentPlan != null) {
                items(currentPlan.meals) { meal ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(meal.icon, fontSize = 36.sp)
                            Spacer(Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    meal.type,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(meal.name, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                                Text(
                                    "${meal.servings} servings",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = { }) {
                                Icon(Icons.Filled.MoreVert, null)
                            }
                        }
                    }
                }
            } else {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🍽️", fontSize = 48.sp)
                            Spacer(Modifier.height(12.dp))
                            Text("No meals planned", fontWeight = FontWeight.Medium)
                            Text(
                                "Tap + to add a meal",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}
