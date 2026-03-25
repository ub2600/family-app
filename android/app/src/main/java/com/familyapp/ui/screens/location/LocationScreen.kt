package com.familyapp.ui.screens.location

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
import com.familyapp.data.model.UserResponse
import com.familyapp.data.repository.FamilyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(private val repo: FamilyRepository) : ViewModel() {
    private val _members = MutableStateFlow<List<UserResponse>>(emptyList())
    val members = _members.asStateFlow()

    init { load() }

    fun load() { viewModelScope.launch { repo.getFamilyLocations().fold(onSuccess = { _members.value = it }, onFailure = {}) } }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationScreen(onNavigateBack: () -> Unit, viewModel: LocationViewModel = hiltViewModel()) {
    val members by viewModel.members.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Family Location", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Filled.ArrowBack, "Back") } }) }
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(vertical = 8.dp)) {
            item {
                Card(Modifier.fillMaxWidth().height(200.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), shape = RoundedCornerShape(16.dp)) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Filled.Map, null, Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.height(8.dp))
                            Text("Map View", fontWeight = FontWeight.Medium)
                            Text("Add a Google Maps API key to enable", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
            item { Text("Family Members", fontWeight = FontWeight.SemiBold, fontSize = 16.sp) }
            if (members.isEmpty()) {
                item { Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), shape = RoundedCornerShape(12.dp)) {
                    Column(Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text("👥", fontSize = 48.sp); Spacer(Modifier.height(8.dp)); Text("No family members found") }
                } }
            }
            items(members) { member ->
                val hasLocation = member.lastLatitude != null
                val colors = listOf(Color(0xFF7C5CFC), Color(0xFF22C55E), Color(0xFF3B82F6), Color(0xFFF97316), Color(0xFFEC4899))
                val color = colors[(member.id % colors.size).toInt()]
                Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), shape = RoundedCornerShape(12.dp)) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(48.dp).clip(CircleShape).background(color), contentAlignment = Alignment.Center) {
                            Text(member.name.first().toString(), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Box(Modifier.offset(x = (-8).dp, y = 16.dp).size(12.dp).clip(CircleShape).background(if (member.active) Color(0xFF22C55E) else Color.Gray).border(2.dp, MaterialTheme.colorScheme.surface, CircleShape))
                        Spacer(Modifier.width(4.dp))
                        Column(Modifier.weight(1f)) {
                            Text(member.name, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                            if (hasLocation) Text("📍 ${member.lastLatitude}, ${member.lastLongitude}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            else Text("No location shared", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            member.lastLocationUpdate?.let { Text("Updated: $it", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f)) }
                        }
                        if (hasLocation) Icon(Icons.Filled.NearMe, null, Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}
