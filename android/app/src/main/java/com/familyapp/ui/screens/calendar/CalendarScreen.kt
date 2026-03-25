package com.familyapp.ui.screens.calendar

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
import com.familyapp.data.model.CalendarEventRequest
import com.familyapp.data.model.CalendarEventResponse
import com.familyapp.data.repository.FamilyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(private val repo: FamilyRepository) : ViewModel() {
    private val _events = MutableStateFlow<List<CalendarEventResponse>>(emptyList())
    val events = _events.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init { loadEvents() }

    fun loadEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            repo.getCalendarEvents().fold(
                onSuccess = { _events.value = it },
                onFailure = { /* silently handle */ }
            )
            _isLoading.value = false
        }
    }

    fun addEvent(title: String, description: String, startTime: String, endTime: String, color: String) {
        viewModelScope.launch {
            repo.createCalendarEvent(CalendarEventRequest(title, description, startTime, endTime, color)).fold(
                onSuccess = { loadEvents() },
                onFailure = { }
            )
        }
    }

    fun deleteEvent(id: Long) {
        viewModelScope.launch {
            repo.deleteCalendarEvent(id).fold(onSuccess = { loadEvents() }, onFailure = { })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(onNavigateToNoticeBoard: () -> Unit, onNavigateToLocation: () -> Unit, viewModel: CalendarViewModel = hiltViewModel()) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var showAddDialog by remember { mutableStateOf(false) }
    val events by viewModel.events.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val eventColors = listOf(Color(0xFF7C5CFC), Color(0xFFEC4899), Color(0xFF22C55E), Color(0xFFF97316), Color(0xFF3B82F6), Color(0xFF14B8A6))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Column { Text("Family Calendar", fontWeight = FontWeight.Bold); Text("Shared Schedule", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) } },
                actions = {
                    IconButton(onClick = onNavigateToNoticeBoard) { Icon(Icons.Filled.Dashboard, "Notice Board") }
                    IconButton(onClick = onNavigateToLocation) { Icon(Icons.Filled.LocationOn, "Location") }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, containerColor = MaterialTheme.colorScheme.primary) { Icon(Icons.Filled.Add, "Add Event") }
        }
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) { Icon(Icons.Filled.ChevronLeft, "Previous") }
                    Text("${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) { Icon(Icons.Filled.ChevronRight, "Next") }
                }
            }
            item {
                Row(Modifier.fillMaxWidth()) {
                    listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { day ->
                        Text(day, Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
                    }
                }
            }
            item { CalendarGrid(currentMonth, selectedDate) { selectedDate = it } }
            item {
                Spacer(Modifier.height(8.dp))
                Text("Events for ${selectedDate.dayOfMonth} ${selectedDate.month.getDisplayName(TextStyle.FULL, Locale.getDefault())}", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            if (events.isEmpty() && !isLoading) {
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), shape = RoundedCornerShape(12.dp)) {
                        Column(Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📅", fontSize = 48.sp); Spacer(Modifier.height(8.dp))
                            Text("No events yet", fontWeight = FontWeight.Medium)
                            Text("Tap + to add your first event", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
            items(events) { event ->
                val color = try { Color(android.graphics.Color.parseColor(event.color)) } catch (e: Exception) { eventColors[0] }
                Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), shape = RoundedCornerShape(12.dp)) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.width(4.dp).height(48.dp).clip(RoundedCornerShape(2.dp)).background(color))
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(event.title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                            Text("${event.startTime} • ${event.createdByName ?: ""}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = { viewModel.deleteEvent(event.id) }) { Icon(Icons.Filled.Delete, "Delete", tint = MaterialTheme.colorScheme.error) }
                    }
                }
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }

    if (showAddDialog) {
        AddEventDialog(onDismiss = { showAddDialog = false }, onAdd = { title, desc, start, end, color ->
            viewModel.addEvent(title, desc, start, end, color); showAddDialog = false
        })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventDialog(onDismiss: () -> Unit, onAdd: (String, String, String, String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("09:00") }
    var endTime by remember { mutableStateOf("10:00") }
    val colors = listOf("#7C5CFC", "#EC4899", "#22C55E", "#F97316", "#3B82F6", "#14B8A6")
    var selectedColor by remember { mutableStateOf(colors[0]) }

    AlertDialog(onDismissRequest = onDismiss, title = { Text("Add Event", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(title, { title = it }, Modifier.fillMaxWidth(), label = { Text("Event Title") }, singleLine = true)
                OutlinedTextField(description, { description = it }, Modifier.fillMaxWidth(), label = { Text("Description (optional)") }, singleLine = true)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(startTime, { startTime = it }, Modifier.weight(1f), label = { Text("Start") }, singleLine = true)
                    OutlinedTextField(endTime, { endTime = it }, Modifier.weight(1f), label = { Text("End") }, singleLine = true)
                }
                Text("Colour", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    colors.forEach { c ->
                        Box(Modifier.size(32.dp).clip(CircleShape).background(Color(android.graphics.Color.parseColor(c)))
                            .then(if (c == selectedColor) Modifier.border(2.dp, Color.White, CircleShape) else Modifier)
                            .clickable { selectedColor = c })
                    }
                }
            }
        },
        confirmButton = { Button(onClick = { if (title.isNotBlank()) onAdd(title, description, startTime, endTime, selectedColor) }, enabled = title.isNotBlank()) { Text("Add") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun CalendarGrid(currentMonth: YearMonth, selectedDate: LocalDate, onDateSelected: (LocalDate) -> Unit) {
    val firstDay = currentMonth.atDay(1)
    val daysInMonth = currentMonth.lengthOfMonth()
    val startDayOfWeek = firstDay.dayOfWeek.value - 1
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        var dayCounter = 1
        for (week in 0..5) {
            if (dayCounter > daysInMonth) break
            Row(Modifier.fillMaxWidth()) {
                for (dow in 0..6) {
                    if (week == 0 && dow < startDayOfWeek || dayCounter > daysInMonth) {
                        Spacer(Modifier.weight(1f).aspectRatio(1f))
                    } else {
                        val day = dayCounter; val date = currentMonth.atDay(day)
                        val isSelected = date == selectedDate; val isToday = date == LocalDate.now()
                        Box(Modifier.weight(1f).aspectRatio(1f).clip(CircleShape).background(when { isSelected -> MaterialTheme.colorScheme.primary; isToday -> MaterialTheme.colorScheme.primary.copy(0.15f); else -> Color.Transparent }).clickable { onDateSelected(date) }, contentAlignment = Alignment.Center) {
                            Text("$day", fontSize = 14.sp, color = when { isSelected -> Color.White; isToday -> MaterialTheme.colorScheme.primary; else -> MaterialTheme.colorScheme.onSurface }, fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal)
                        }
                        dayCounter++
                    }
                }
            }
        }
    }
}
