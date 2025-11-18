package com.dietpizza.byakugan.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dietpizza.byakugan.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ByakuganTopBar(onSettingsClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Library",
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_settings),
                    contentDescription = "Settings",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
