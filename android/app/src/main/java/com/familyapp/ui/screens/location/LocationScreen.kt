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

data class FamilyMemberLocation(
    val name: String, val role: String, val lastSeen: String,
    val location: String, val isOnline: Boolean, val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationScreen(onNavigateBack: () -> Unit) {
    val members = remember {
        listOf(
            FamilyMemberLocation("Mum", "Parent", "Just now", "Home", true, Color(0xFF7C5CFC)),
            FamilyMemberLocation("Dad", "Parent", "5 min ago", "Tesco, High Street", true, Color(0xFF3B82F6)),
            FamilyMemberLocation("Tom", "Child", "15 min ago", "St Mary's School", true, Color(0xFF22C55E)),
            FamilyMemberLocation("Emma", "Child", "2 hours ago", "Home", false, Color(0xFFEC4899))
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Family Location", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Filled.ArrowBack, "Back") } }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            // Map placeholder
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().height(250.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Filled.Map, null, Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f))
                            Spacer(Modifier.height(8.dp))
                            Text("Map View", fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Add your Google Maps API key", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f))
                        }
                    }
                }
            }

            item { Text("Family Members", fontWeight = FontWeight.SemiBold, fontSize = 16.sp) }

            items(members) { member ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box {
                            Box(Modifier.size(48.dp).clip(CircleShape).background(member.color.copy(0.2f)),
                                contentAlignment = Alignment.Center) {
                                Text(member.name.first().toString(), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = member.color)
                            }
                            if (member.isOnline) {
                                Box(Modifier.size(14.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant).align(Alignment.BottomEnd)) {
                                    Box(Modifier.size(10.dp).clip(CircleShape).background(Color(0xFF22C55E)).align(Alignment.Center))
                                }
                            }
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(member.name, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                                Spacer(Modifier.width(6.dp))
                                Text(member.role, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.background(MaterialTheme.colorScheme.outline.copy(0.2f), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 1.dp))
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.LocationOn, null, Modifier.size(14.dp), tint = member.color)
                                Spacer(Modifier.width(4.dp))
                                Text(member.location, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text("Last seen: ${member.lastSeen}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f))
                        }
                        Icon(Icons.Filled.Navigation, null, tint = member.color, modifier = Modifier.size(24.dp))
                    }
                }
            }
        }
    }
}
