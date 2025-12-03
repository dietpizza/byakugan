package com.dietpizza.byakugan.components.reader

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MangaLoadingDialog(title: String, progress: Float) {
    BasicAlertDialog(
        content = {
            Card {
                Row(
                    modifier = Modifier.padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularWavyProgressIndicator(
                        progress = { progress / 100 }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        "Loading: $title",
                        style = MaterialTheme.typography.bodyLargeEmphasized,
                        maxLines = 2
                    )
                }
            }
        },
        onDismissRequest = {},
    )
}
