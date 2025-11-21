package com.dietpizza.byakugan.composables.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dietpizza.byakugan.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    title: String,
    progress: Float? = null,
    onSettingsClick: (() -> Unit)?,
    onRefreshClick: (() -> Unit)?
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 0.dp
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 16.dp)
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
            Row(
                modifier = Modifier
                    .height(2.dp)
                    .fillMaxWidth()
            ) {
                if (progress != null && progress > 1f)
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        progress = { progress / 100 },
                    )
            }
        }
    }
}

@Composable
fun RoundIconButton(@DrawableRes res: Int, onClick: () -> Unit) {
    FilledTonalIconButton(
        onClick,
        modifier = Modifier
            .size(36.dp)
    ) {
        Icon(
            painter = painterResource(id = res),
            contentDescription = "Round Icon Button",
            modifier = Modifier.size(24.dp)
        )
    }
}