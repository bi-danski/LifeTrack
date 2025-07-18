package com.example.lifetrack.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lifetrack.model.repository.UserRepositoryImpl
import kotlinx.coroutines.launch

@Composable
fun RestoreScreen(navController: NavController, userRepository: UserRepositoryImpl) {
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var resetSuccess by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Reset Password",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        isLoading = true
                        errorMessage = null
                        coroutineScope.launch {
                            val result = userRepository.sendPasswordReset(email.text)
                            isLoading = false
                            when (result) {
                                is com.example.lifetrack.model.data.AuthResult.Success -> {
                                    resetSuccess = true
                                    snackbarHostState.showSnackbar("Password reset link sent to your email.")
                                }
                                is com.example.lifetrack.model.data.AuthResult.Failure -> {
                                    resetSuccess = false
                                    errorMessage = result.message
                                    snackbarHostState.showSnackbar("Error: ${result.message}")
                                }
                                else -> {}
                            }
                        }
                    },
                    enabled = !isLoading && email.text.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(text = if (isLoading) "Sending..." else "Send Reset Link")
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                if (resetSuccess && !isLoading) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Check your email to reset your password.",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                TextButton(onClick = { navController.navigate("login") }) {
                    Text("Back to Login")
                }
            }
        }
    }
}