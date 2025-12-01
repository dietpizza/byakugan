package com.dietpizza.byakugan.components.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dietpizza.byakugan.models.SortBy
import com.dietpizza.byakugan.models.SortOrder
import com.dietpizza.byakugan.models.SortSettings

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SortSettingsDialog(
    currentSettings: SortSettings,
    onDismiss: () -> Unit,
    onConfirm: (SortSettings) -> Unit
) {
    var selectedSortBy by remember { mutableStateOf(currentSettings.sortBy) }
    var selectedSortOrder by remember { mutableStateOf(currentSettings.sortOrder) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                shape = ButtonGroupDefaults.connectedTrailingButtonShape,
                onClick = {
                    onConfirm(SortSettings(selectedSortBy, selectedSortOrder))
                    onDismiss()
                }
            ) {
                Text("Apply")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = ButtonGroupDefaults.connectedLeadingButtonShape
            ) {
                Text("Cancel")
            }
        },
        title = {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        },

        text = {
            Column {

                Text(
                    text = "Sort By",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Sort By options
                SortOption(
                    label = "Name of file",
                    selected = selectedSortBy == SortBy.NAME,
                    onClick = { selectedSortBy = SortBy.NAME }
                )
                SortOption(
                    label = "Number of Pages",
                    selected = selectedSortBy == SortBy.PAGES,
                    onClick = { selectedSortBy = SortBy.PAGES }
                )
                SortOption(
                    label = "Last Modified",
                    selected = selectedSortBy == SortBy.TIME,
                    onClick = { selectedSortBy = SortBy.TIME }
                )

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Order By",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))

                SortOption(
                    label = "Ascending",
                    selected = selectedSortOrder == SortOrder.ASCENDING,
                    onClick = { selectedSortOrder = SortOrder.ASCENDING }
                )
                SortOption(
                    label = "Descending",
                    selected = selectedSortOrder == SortOrder.DESCENDING,
                    onClick = { selectedSortOrder = SortOrder.DESCENDING }
                )
            }
        }
    )
}

@Composable
private fun SortOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

