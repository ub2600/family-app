package com.familyapp.ui.navigation

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.familyapp.ui.screens.auth.LoginScreen
import com.familyapp.ui.screens.auth.RegisterScreen
import com.familyapp.ui.screens.calendar.CalendarScreen
import com.familyapp.ui.screens.chat.ChatScreen
import com.familyapp.ui.screens.expenses.ExpensesScreen
import com.familyapp.ui.screens.lists.ListsScreen
import com.familyapp.ui.screens.meals.MealPlannerScreen
import com.familyapp.ui.screens.noticeboard.NoticeBoardScreen
import com.familyapp.ui.screens.location.LocationScreen

sealed class Screen(val route: String, val title: String, val icon: ImageVector, val outlinedIcon: ImageVector) {
    object Calendar : Screen("calendar", "Calendar", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth)
    object Lists : Screen("lists", "Lists", Icons.Filled.Checklist, Icons.Outlined.Checklist)
    object Expenses : Screen("expenses", "Money", Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet)
    object Meals : Screen("meals", "Meals", Icons.Filled.Restaurant, Icons.Outlined.Restaurant)
    object Chat : Screen("chat", "Chat", Icons.Filled.Chat, Icons.Outlined.Chat)
}

val bottomNavItems = listOf(Screen.Calendar, Screen.Lists, Screen.Expenses, Screen.Meals, Screen.Chat)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamilyAppNavigation() {
    val navController = rememberNavController()
    var isAuthenticated by remember { mutableStateOf(false) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in bottomNavItems.map { it.route } + listOf("noticeboard", "location")

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp
                ) {
                    bottomNavItems.forEach { screen ->
                        val selected = currentRoute == screen.route
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    if (selected) screen.icon else screen.outlinedIcon,
                                    contentDescription = screen.title
                                )
                            },
                            label = { Text(screen.title, style = MaterialTheme.typography.labelSmall) },
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = if (isAuthenticated) "calendar" else "login",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("login") {
                LoginScreen(
                    onLoginSuccess = {
                        isAuthenticated = true
                        navController.navigate("calendar") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onNavigateToRegister = { navController.navigate("register") }
                )
            }
            composable("register") {
                RegisterScreen(
                    onRegisterSuccess = {
                        isAuthenticated = true
                        navController.navigate("calendar") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("calendar") {
                CalendarScreen(
                    onNavigateToNoticeBoard = { navController.navigate("noticeboard") },
                    onNavigateToLocation = { navController.navigate("location") }
                )
            }
            composable("lists") { ListsScreen() }
            composable("expenses") { ExpensesScreen() }
            composable("meals") { MealPlannerScreen() }
            composable("chat") { ChatScreen() }
            composable("noticeboard") { NoticeBoardScreen(onNavigateBack = { navController.popBackStack() }) }
            composable("location") { LocationScreen(onNavigateBack = { navController.popBackStack() }) }
        }
    }
}
