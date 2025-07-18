package com.example.lifetrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lifetrack.presenter.AuthPresenter
import com.example.lifetrack.ui.components.LTBrandAppBar
import com.example.lifetrack.ui.state.UIState
import com.example.lifetrack.view.AuthView
import kotlinx.coroutines.launch


@Composable
fun LoginScreen(navController: NavController, presenter: AuthPresenter) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    var uiState by remember { mutableStateOf<UIState>(UIState.Idle) }

    LaunchedEffect(presenter) {
        presenter.view = object : AuthView {
            override fun showLoading(isLoading: Boolean, message: String?) {
                uiState = if (isLoading) UIState.Loading else UIState.Idle
            }

            override fun showError(message: String) {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(message)
                }
            }

            override fun onAuthSuccess() {
                uiState = UIState.Success
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) {
                Snackbar(
                    snackbarData = it,
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            LTBrandAppBar(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 32.dp),

            )
//            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.75f)
                    .wrapContentHeight()
                    .align(Alignment.BottomCenter),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(0.85f) // 85% width
                )
                Spacer(modifier = Modifier.height(28.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val icon =
                            if (passwordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                            Icon(imageVector = icon, contentDescription = "Toggle Password Visibility")
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(0.85f) // 85% width
                )
                Spacer(modifier = Modifier.height(48.dp))

                Button(
                    onClick = {
                        if (email.isNotEmpty() && password.isNotEmpty()) {
                            presenter.login(email, password)
                        } else {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Please fill in all fields.")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.85f) // 85% width
                        .height(50.dp)
                        .clip(RoundedCornerShape(12.dp)),

                    enabled = uiState != UIState.Loading
                ) {
                    if (uiState == UIState.Loading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(text = "Login", style = MaterialTheme.typography.labelLarge)
                    }
                }
//                Spacer(modifier = Modifier.height(24.dp))
                TextButton(onClick = { navController.navigate("reset") }) {
                    Text(
                        text = "Forgot Password?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp, top = 24.dp)
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don’t have an account?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(4.dp))
                TextButton(onClick = { navController.navigate("signup") }) {
                    Text(
                        text = "Sign Up",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
