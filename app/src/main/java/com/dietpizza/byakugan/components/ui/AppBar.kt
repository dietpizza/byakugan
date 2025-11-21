package com.dietpizza.byakugan.components.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dietpizza.byakugan.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    title: String,
    onSettingsClick: (() -> Unit)?,
    onRefreshClick: (() -> Unit)?,
    scrollBehavior: androidx.compose.material3.TopAppBarScrollBehavior? = null
) {
    TopAppBar(
        modifier = Modifier.fillMaxWidth(),
        colors = TopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface
        ),
        scrollBehavior = scrollBehavior,
        title = { AppBarTitle(title, onSettingsClick, onRefreshClick) }
    )
}

@Composable
fun AppBarTitle(
    title: String,
    onSettingsClick: (() -> Unit)?,
    onRefreshClick: (() -> Unit)?
) {
    Row(
        modifier = Modifier
            .padding(end = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Row {
            if (onRefreshClick != null) {
                Spacer(modifier = Modifier.width(16.dp))
                RoundIconButton(R.drawable.ic_refresh, onRefreshClick)
            }
            if (onSettingsClick != null) {
                Spacer(modifier = Modifier.width(16.dp))
                RoundIconButton(R.drawable.ic_settings, onSettingsClick)
            }
        }
    }
}
