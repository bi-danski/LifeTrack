package com.example.lifetrack.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lifetrack.ui.components.HealthSummaryCard
import com.example.lifetrack.ui.components.LifeTrackTopBar
import com.example.lifetrack.ui.components.QuickActionsRow

@Composable
fun HomeScreen(
    navController: NavController,
    onEmergency: () -> Unit,
    onSearch: () -> Unit,
    onAlma: () -> Unit
) {
    Scaffold(
        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.background,
        topBar = { LifeTrackTopBar(onMenuClick = { navController.navigate("menu") }, onProfileClick = { navController.navigate("profile") }) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    "Welcome Back!",
                    style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Justify)
            }
            item {
                HealthSummaryCard()
            }
            item {
                QuickActionsRow(
                    onEmergencyClick = onEmergency,
                    onSearchClick = onSearch,
                    onAlmaClick = onAlma
                )
            }
        }
    }
}
