package com.example.lifetrack.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.lifetrack.R
import kotlinx.coroutines.delay
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight

@Composable
fun LTBrandAppBar(modifier: Modifier = Modifier) {
    var scale by remember { mutableStateOf(1f) }

    LaunchedEffect(Unit) {
        while(true) {
            scale = 1.02f
            delay(500)
            scale = 1f
            delay(500)
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Image(
            painter = painterResource(R.drawable.lifetrack_icon_logo_dark),
            contentDescription = "LifeTrack Logo",
            modifier = Modifier
                .size(100.dp)
                .graphicsLayer (
                    scaleX = scale,
                    scaleY = scale,
                    compositingStrategy = CompositingStrategy.Offscreen
                )
        )
        Text(
            text = "LifeTrack",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Better Healthcare , Simplified",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}