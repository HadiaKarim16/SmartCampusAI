package com.smartcampus.ai.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcampus.ai.domain.model.UiState
import com.smartcampus.ai.ui.theme.SmartCampusColors

// ─────────────────────────────────────────────
//  LOGIN SCREEN
// ─────────────────────────────────────────────
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onSignupClick: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val loginState by viewModel.loginState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }

    LaunchedEffect(loginState) {
        if (loginState is UiState.Success) {
            viewModel.clearStates()
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        SmartCampusColors.Background,
                        SmartCampusColors.Surface,
                        SmartCampusColors.Background
                    )
                )
            )
    ) {
        // Background decorations
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = SmartCampusColors.Primary.copy(alpha = 0.06f),
                radius = 300.dp.toPx(),
                center = Offset(size.width * 0.8f, size.height * 0.1f)
            )
            drawCircle(
                color = SmartCampusColors.Secondary.copy(alpha = 0.05f),
                radius = 250.dp.toPx(),
                center = Offset(size.width * 0.1f, size.height * 0.85f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(60.dp))

            // Logo / Icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        Brush.linearGradient(
                            listOf(SmartCampusColors.Primary, SmartCampusColors.Secondary)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.School,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(42.dp)
                )
            }

            Spacer(Modifier.height(24.dp))
            Text(
                text = "SmartCampus AI",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Text(
                text = "Your intelligent academic companion",
                style = MaterialTheme.typography.bodyMedium,
                color = SmartCampusColors.OnSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(48.dp))

            // Login Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SmartCampusColors.Surface),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Welcome Back 👋",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Sign in to continue",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SmartCampusColors.OnSurfaceVariant
                    )

                    Spacer(Modifier.height(4.dp))

                    // Email Field
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, null, tint = SmartCampusColors.Primary)
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SmartCampusColors.Primary,
                            unfocusedBorderColor = SmartCampusColors.SurfaceVariant
                        )
                    )

                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, null, tint = SmartCampusColors.Primary)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = SmartCampusColors.OnSurfaceVariant
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                viewModel.login(email, password)
                            }
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SmartCampusColors.Primary,
                            unfocusedBorderColor = SmartCampusColors.SurfaceVariant
                        )
                    )

                    // Remember Me
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Checkbox(
                                checked = rememberMe,
                                onCheckedChange = { rememberMe = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = SmartCampusColors.Primary
                                )
                            )
                            Text(
                                text = "Remember me",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        TextButton(onClick = {}) {
                            Text(
                                text = "Forgot password?",
                                color = SmartCampusColors.Primary,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    // Error Message
                    AnimatedVisibility(visible = loginState is UiState.Error) {
                        val errorMsg = (loginState as? UiState.Error)?.message ?: ""
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    SmartCampusColors.Error.copy(alpha = 0.1f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Error,
                                null,
                                tint = SmartCampusColors.Error,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = errorMsg,
                                color = SmartCampusColors.Error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    // Login Button
                    Button(
                        onClick = { viewModel.login(email, password) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SmartCampusColors.Primary
                        ),
                        enabled = loginState !is UiState.Loading
                    ) {
                        if (loginState is UiState.Loading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Sign In",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Sign Up Link
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don't have an account? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SmartCampusColors.OnSurfaceVariant
                )
                TextButton(onClick = onSignupClick) {
                    Text(
                        text = "Sign Up",
                        color = SmartCampusColors.Primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

// ─────────────────────────────────────────────
//  SIGNUP SCREEN
// ─────────────────────────────────────────────
@Composable
fun SignupScreen(
    onSignupSuccess: () -> Unit,
    onLoginClick: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val signupState by viewModel.signupState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(signupState) {
        if (signupState is UiState.Success) {
            viewModel.clearStates()
            onSignupSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(SmartCampusColors.Background, SmartCampusColors.Surface)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(40.dp))

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        Brush.linearGradient(
                            listOf(SmartCampusColors.Secondary, SmartCampusColors.Primary)
                        ),
                        RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.PersonAdd, null, tint = Color.White, modifier = Modifier.size(32.dp))
            }

            Spacer(Modifier.height(20.dp))
            Text(
                "Create Account",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                "Start your academic journey",
                style = MaterialTheme.typography.bodyMedium,
                color = SmartCampusColors.OnSurfaceVariant
            )
            Spacer(Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SmartCampusColors.Surface),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    listOf(
                        Triple("Full Name", Icons.Default.Person, name) to { v: String -> name = v },
                        Triple("Email", Icons.Default.Email, email) to { v: String -> email = v }
                    ).forEach { (info, onChange) ->
                        val (label, icon, value) = info
                        OutlinedTextField(
                            value = value,
                            onValueChange = onChange,
                            label = { Text(label) },
                            leadingIcon = { Icon(icon, null, tint = SmartCampusColors.Primary) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SmartCampusColors.Primary,
                                unfocusedBorderColor = SmartCampusColors.SurfaceVariant
                            )
                        )
                    }

                    // Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = SmartCampusColors.Primary) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    null,
                                    tint = SmartCampusColors.OnSurfaceVariant
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SmartCampusColors.Primary,
                            unfocusedBorderColor = SmartCampusColors.SurfaceVariant
                        )
                    )

                    // Confirm Password
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm Password") },
                        leadingIcon = { Icon(Icons.Default.LockReset, null, tint = SmartCampusColors.Primary) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                viewModel.signup(name, email, password, confirmPassword)
                            }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SmartCampusColors.Primary,
                            unfocusedBorderColor = SmartCampusColors.SurfaceVariant
                        )
                    )

                    // Error Message
                    AnimatedVisibility(visible = signupState is UiState.Error) {
                        val errorMsg = (signupState as? UiState.Error)?.message ?: ""
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    SmartCampusColors.Error.copy(alpha = 0.1f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Error, null, tint = SmartCampusColors.Error, modifier = Modifier.size(16.dp))
                            Text(errorMsg, color = SmartCampusColors.Error, style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    Button(
                        onClick = { viewModel.signup(name, email, password, confirmPassword) },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SmartCampusColors.Primary),
                        enabled = signupState !is UiState.Loading
                    ) {
                        if (signupState is UiState.Loading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Create Account", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Already have an account? ", color = SmartCampusColors.OnSurfaceVariant)
                TextButton(onClick = onLoginClick) {
                    Text("Sign In", color = SmartCampusColors.Primary, fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
