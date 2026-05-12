package com.dietpizza.byakugan.components.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dietpizza.byakugan.components.ui.ConnectedRadioButton
import com.dietpizza.byakugan.components.ui.HorizontalConnectedRadioButtonShape
import com.dietpizza.byakugan.components.ui.VerticalConnectedRadioButtonShape
import com.dietpizza.byakugan.models.SortBy
import com.dietpizza.byakugan.models.SortOrder
import com.dietpizza.byakugan.models.SortSettings

private const val TAG = "LibrarySettingsDialog"

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LibrarySettingsDialog(
    isVisible: Boolean,
    currentSettings: SortSettings?,
    onDismiss: () -> Unit,
    onConfirm: (SortSettings) -> Unit
) {
    // Handle null currentSettings
    if (currentSettings == null) {
        return
    }

    var selectedSortBy by remember { mutableStateOf(currentSettings.sortBy) }
    var selectedSortOrder by remember { mutableStateOf(currentSettings.sortOrder) }

    val onCommitChanges: () -> Unit = {
        onDismiss()
        onConfirm(SortSettings(selectedSortBy, selectedSortOrder))
    }

    val onReset: () -> Unit = {
        onDismiss()
        selectedSortBy = currentSettings.sortBy
        selectedSortOrder = currentSettings.sortOrder
    }

    if (isVisible) {
        BasicAlertDialog(onDismissRequest = onDismiss) {
            Card(
                elevation = CardDefaults.cardElevation(6.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            ) {
                Column(
                    Modifier
                        .padding(horizontal = 24.dp)
                        .padding(top = 24.dp, bottom = 16.dp)
                ) {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    ConnectedRadioButton(
                        label = "File Name",
                        selected = selectedSortBy == SortBy.NAME,
                        onClick = { selectedSortBy = SortBy.NAME },
                        modifier = Modifier.fillMaxWidth(),
                        shape = VerticalConnectedRadioButtonShape.TopButtonShape
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    ConnectedRadioButton(
                        label = "Number of Pages",
                        selected = selectedSortBy == SortBy.PAGES,
                        onClick = { selectedSortBy = SortBy.PAGES },
                        modifier = Modifier.fillMaxWidth(),
                        shape = VerticalConnectedRadioButtonShape.MiddleButtonShape
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    ConnectedRadioButton(
                        label = "Last Modified",
                        selected = selectedSortBy == SortBy.TIME,
                        onClick = { selectedSortBy = SortBy.TIME },
                        modifier = Modifier.fillMaxWidth(),
                        shape = VerticalConnectedRadioButtonShape.BottomButtonShape
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val (ascendingLabel, descendingLabel) = when (selectedSortBy) {
                        SortBy.NAME -> "A to Z" to "Z to A"
                        SortBy.TIME -> "Oldest" to "Newest"
                        SortBy.PAGES -> "Ascending" to "Descending"
                    }

                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ConnectedRadioButton(
                            modifier = Modifier.weight(1f),
                            shape = HorizontalConnectedRadioButtonShape.TopButtonShape,
                            label = ascendingLabel,
                            selected = selectedSortOrder == SortOrder.ASCENDING,
                            onClick = { selectedSortOrder = SortOrder.ASCENDING },
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        ConnectedRadioButton(
                            modifier = Modifier.weight(1f),
                            shape = HorizontalConnectedRadioButtonShape.BottomButtonShape,
                            label = descendingLabel,
                            selected = selectedSortOrder == SortOrder.DESCENDING,
                            onClick = { selectedSortOrder = SortOrder.DESCENDING },
                        )
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 16.dp)
                ) {
                    TextButton(onClick = onReset) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = onCommitChanges) {
                        Text("Done")
                    }
                }
            }
        }
    }
}


