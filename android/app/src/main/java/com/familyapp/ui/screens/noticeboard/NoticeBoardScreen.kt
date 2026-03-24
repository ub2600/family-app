package com.familyapp.ui.screens.noticeboard

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class NoticeUI(val id: Long, val title: String, val content: String, val pinned: Boolean, val color: Long, val createdBy: String, val date: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoticeBoardScreen(onNavigateBack: () -> Unit) {
    val notices = remember {
        mutableStateListOf(
            NoticeUI(1, "🎂 Emma's Birthday!", "Don't forget Emma's birthday party on Saturday! Cake is ordered from Waitrose.", true, 0xFFFFD700, "Mum", "23 Mar"),
            NoticeUI(2, "🔧 Boiler Service", "Engineer coming Tuesday between 9-12. Someone needs to be home.", true, 0xFFF97316, "Dad", "22 Mar"),
            NoticeUI(3, "📅 Parent's Evening", "Tom's parent's evening is next Thursday 6-8pm. Please book a slot.", false, 0xFF7C5CFC, "Mum", "21 Mar"),
            NoticeUI(4, "🏥 Dentist Reminder", "Everyone's dental check-ups are booked for 15th April.", false, 0xFF22C55E, "Dad", "20 Mar"),
            NoticeUI(5, "🐕 Dog Walker", "New dog walker starts Monday. She'll come at 2pm on weekdays.", false, 0xFFEC4899, "Mum", "19 Mar")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notice Board", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Filled.ArrowBack, "Back") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {}, containerColor = MaterialTheme.colorScheme.primary) {
                Icon(Icons.Filled.Add, "Add Notice")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            val pinned = notices.filter { it.pinned }
            val unpinned = notices.filter { !it.pinned }

            if (pinned.isNotEmpty()) {
                item { Text("📌 Pinned", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                items(pinned) { notice -> NoticeCard(notice) }
            }
            if (unpinned.isNotEmpty()) {
                item { Text("Recent", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                items(unpinned) { notice -> NoticeCard(notice) }
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun NoticeCard(notice: NoticeUI) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column {
            Box(Modifier.fillMaxWidth().height(4.dp).background(Color(notice.color)))
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(notice.title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, modifier = Modifier.weight(1f))
                    if (notice.pinned) Icon(Icons.Filled.PushPin, null, tint = Color(0xFFFFD700), modifier = androidx.compose.ui.Modifier.size(18.dp))
                }
                Spacer(Modifier.height(6.dp))
                Text(notice.content, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 20.sp)
                Spacer(Modifier.height(8.dp))
                Text("${notice.createdBy} • ${notice.date}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f))
            }
        }
    }
}
