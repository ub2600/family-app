package com.familyapp.ui.screens.auth

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.familyapp.data.repository.FamilyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val repo: FamilyRepository) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true; _error.value = null
            repo.login(email, password).fold(
                onSuccess = { auth -> repo.saveToken(auth); _isLoading.value = false; onSuccess() },
                onFailure = { _error.value = it.message ?: "Login failed"; _isLoading.value = false }
            )
        }
    }

    fun register(name: String, email: String, password: String, familyName: String?, inviteCode: String?, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true; _error.value = null
            repo.register(name, email, password, familyName, inviteCode).fold(
                onSuccess = { auth -> repo.saveToken(auth); _isLoading.value = false; onSuccess() },
                onFailure = { _error.value = it.message ?: "Registration failed"; _isLoading.value = false }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onNavigateToRegister: () -> Unit, viewModel: AuthViewModel = hiltViewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val fieldColors = OutlinedTextFieldDefaults.colors(
        unfocusedBorderColor = Color.White.copy(alpha = 0.15f), unfocusedTextColor = Color.White,
        focusedTextColor = Color.White, unfocusedLabelColor = Color.White.copy(alpha = 0.5f),
        focusedLabelColor = Color(0xFF7C5CFC), unfocusedLeadingIconColor = Color.White.copy(alpha = 0.5f)
    )

    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF0F0C29), Color(0xFF302B63), Color(0xFF24243E))))) {
        Column(Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Spacer(Modifier.height(48.dp))
            Text("👨‍👩‍👧‍👦", fontSize = 64.sp)
            Spacer(Modifier.height(16.dp))
            Text("Welcome Back", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Sign in to your family", fontSize = 16.sp, color = Color.White.copy(alpha = 0.6f))
            Spacer(Modifier.height(40.dp))

            error?.let {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFF85149).copy(alpha = 0.15f)), shape = RoundedCornerShape(12.dp)) {
                    Text(it, Modifier.padding(16.dp).fillMaxWidth(), color = Color(0xFFF85149), fontSize = 14.sp)
                }
                Spacer(Modifier.height(16.dp))
            }

            OutlinedTextField(email, { email = it }, Modifier.fillMaxWidth(), label = { Text("Email") }, leadingIcon = { Icon(Icons.Filled.Email, null) },
                shape = RoundedCornerShape(12.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), singleLine = true, colors = fieldColors)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(password, { password = it }, Modifier.fillMaxWidth(), label = { Text("Password") }, leadingIcon = { Icon(Icons.Filled.Lock, null) },
                trailingIcon = { IconButton(onClick = { passwordVisible = !passwordVisible }) { Icon(if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, null, tint = Color.White.copy(0.5f)) } },
                shape = RoundedCornerShape(12.dp), visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(), singleLine = true, colors = fieldColors)
            Spacer(Modifier.height(32.dp))

            Button(onClick = { viewModel.login(email, password, onLoginSuccess) }, Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C5CFC)), enabled = email.isNotBlank() && password.isNotBlank() && !isLoading) {
                if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                else { Icon(Icons.Filled.Login, null, Modifier.size(20.dp)); Spacer(Modifier.width(8.dp)); Text("Sign In", fontSize = 16.sp, fontWeight = FontWeight.SemiBold) }
            }
            Spacer(Modifier.height(24.dp))
            TextButton(onClick = onNavigateToRegister) { Text("Don't have an account? Sign Up", color = Color(0xFFA78BFA), fontSize = 14.sp) }
            Spacer(Modifier.height(48.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(onRegisterSuccess: () -> Unit, onNavigateBack: () -> Unit, viewModel: AuthViewModel = hiltViewModel()) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var familyName by remember { mutableStateOf("") }
    var inviteCode by remember { mutableStateOf("") }
    var useInviteCode by remember { mutableStateOf(false) }
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val fieldColors = OutlinedTextFieldDefaults.colors(
        unfocusedBorderColor = Color.White.copy(alpha = 0.15f), unfocusedTextColor = Color.White,
        focusedTextColor = Color.White, unfocusedLabelColor = Color.White.copy(alpha = 0.5f),
        focusedLabelColor = Color(0xFF7C5CFC), unfocusedLeadingIconColor = Color.White.copy(alpha = 0.5f)
    )

    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF0F0C29), Color(0xFF302B63), Color(0xFF24243E))))) {
        Column(Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(48.dp))
            Text("👨‍👩‍👧‍👦", fontSize = 48.sp)
            Spacer(Modifier.height(12.dp))
            Text("Create Account", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Join your family today", fontSize = 14.sp, color = Color.White.copy(alpha = 0.6f))
            Spacer(Modifier.height(32.dp))

            error?.let {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFF85149).copy(alpha = 0.15f)), shape = RoundedCornerShape(12.dp)) {
                    Text(it, Modifier.padding(16.dp).fillMaxWidth(), color = Color(0xFFF85149), fontSize = 14.sp)
                }
                Spacer(Modifier.height(12.dp))
            }

            OutlinedTextField(name, { name = it }, Modifier.fillMaxWidth(), label = { Text("Full Name") }, leadingIcon = { Icon(Icons.Filled.Person, null) },
                shape = RoundedCornerShape(12.dp), singleLine = true, colors = fieldColors)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(email, { email = it }, Modifier.fillMaxWidth(), label = { Text("Email") }, leadingIcon = { Icon(Icons.Filled.Email, null) },
                shape = RoundedCornerShape(12.dp), singleLine = true, colors = fieldColors, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(password, { password = it }, Modifier.fillMaxWidth(), label = { Text("Password") }, leadingIcon = { Icon(Icons.Filled.Lock, null) },
                shape = RoundedCornerShape(12.dp), singleLine = true, colors = fieldColors, visualTransformation = PasswordVisualTransformation())
            Spacer(Modifier.height(20.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(!useInviteCode, { useInviteCode = false }, { Text("Create Family") }, Modifier.weight(1f))
                FilterChip(useInviteCode, { useInviteCode = true }, { Text("Join Family") }, Modifier.weight(1f))
            }
            Spacer(Modifier.height(12.dp))

            if (useInviteCode) {
                OutlinedTextField(inviteCode, { inviteCode = it }, Modifier.fillMaxWidth(), label = { Text("Invite Code") },
                    leadingIcon = { Icon(Icons.Filled.Key, null) }, shape = RoundedCornerShape(12.dp), singleLine = true, colors = fieldColors)
            } else {
                OutlinedTextField(familyName, { familyName = it }, Modifier.fillMaxWidth(), label = { Text("Family Name") },
                    leadingIcon = { Icon(Icons.Filled.Home, null) }, shape = RoundedCornerShape(12.dp), singleLine = true, colors = fieldColors)
            }
            Spacer(Modifier.height(32.dp))

            Button(onClick = {
                viewModel.register(name, email, password, if (!useInviteCode) familyName else null, if (useInviteCode) inviteCode else null, onRegisterSuccess)
            }, Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C5CFC)), enabled = name.isNotBlank() && email.isNotBlank() && password.isNotBlank() && !isLoading) {
                if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                else Text("Create Account", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(16.dp))
            TextButton(onClick = onNavigateBack) { Text("Already have an account? Sign In", color = Color(0xFFA78BFA)) }
            Spacer(Modifier.height(48.dp))
        }
    }
}
