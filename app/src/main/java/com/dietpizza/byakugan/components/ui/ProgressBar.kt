package com.dietpizza.byakugan.components.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp

@Composable
fun ProgressBar(isVisible: Boolean, progress: Float) {
    Row(
        modifier = Modifier
            .height(2.dp)
            .fillMaxWidth()
    ) {
        if (isVisible)
            LinearProgressIndicator(
                strokeCap = StrokeCap.Square,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                progress = { progress / 100 },
            )
    }
}

