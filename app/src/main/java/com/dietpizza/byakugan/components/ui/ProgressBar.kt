package com.dietpizza.byakugan.components.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProgressBar(isVisible: Boolean, progress: Float) {
    Row(
        modifier = Modifier
            .height(6.dp)
            .fillMaxWidth()
    ) {
        if (isVisible)
            LinearWavyProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                progress = { progress / 100 },
            )
    }
}

