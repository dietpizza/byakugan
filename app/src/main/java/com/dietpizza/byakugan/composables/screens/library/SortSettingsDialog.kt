package com.dietpizza.byakugan.composables.screens.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortSettingsDialog(
    currentSettings: SortSettings,
    onDismiss: () -> Unit,
    onConfirm: (SortSettings) -> Unit
) {
    var selectedSortBy by remember { mutableStateOf(currentSettings.sortBy) }
    var selectedSortOrder by remember { mutableStateOf(currentSettings.sortOrder) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Sort Settings",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Sort By",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))

            // Sort By options
            SortOption(
                label = "Title",
                selected = selectedSortBy == SortBy.NAME,
                onClick = { selectedSortBy = SortBy.NAME }
            )
            SortOption(
                label = "Number of pages",
                selected = selectedSortBy == SortBy.PAGES,
                onClick = { selectedSortBy = SortBy.PAGES }
            )
            SortOption(
                label = "Date Added",
                selected = selectedSortBy == SortBy.TIME,
                onClick = { selectedSortBy = SortBy.TIME }
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Order",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(6.dp))

            // Sort Order options - dynamic labels based on sort type
            val (ascLabel, descLabel) = when (selectedSortBy) {
                SortBy.NAME -> "A-Z (Alphabetical)" to "A-Z (Reverse)"
                SortBy.PAGES -> "Smallest first" to "Largest first"
                SortBy.TIME -> "Oldest first" to "Newest first"
            }

            SortOption(
                label = descLabel,
                selected = selectedSortOrder == SortOrder.DESCENDING,
                onClick = { selectedSortOrder = SortOrder.DESCENDING }
            )
            SortOption(
                label = ascLabel,
                selected = selectedSortOrder == SortOrder.ASCENDING,
                onClick = { selectedSortOrder = SortOrder.ASCENDING }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.padding(8.dp))
                TextButton(
                    onClick = {
                        onConfirm(SortSettings(selectedSortBy, selectedSortOrder))
                        onDismiss()
                    }
                ) {
                    Text("Apply")
                }
            }
        }
    }
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

