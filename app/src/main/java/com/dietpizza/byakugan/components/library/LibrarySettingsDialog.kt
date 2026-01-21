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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LibrarySettingsDialog(
    currentSettings: SortSettings,
    onDismiss: () -> Unit,
    onConfirm: (SortSettings) -> Unit
) {
    var selectedSortBy by remember { mutableStateOf(currentSettings.sortBy) }
    var selectedSortOrder by remember { mutableStateOf(currentSettings.sortOrder) }

    fun onSelectValue(sortBy: SortBy = selectedSortBy, sortOrder: SortOrder = selectedSortOrder) {
        selectedSortBy = sortBy
        selectedSortOrder = sortOrder

        onDismiss()
        onConfirm(SortSettings(selectedSortBy, selectedSortOrder))
    }


    BasicAlertDialog(onDismissRequest = onDismiss) {
        Card(
            elevation = CardDefaults.cardElevation(6.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            )
        ) {
            Column(Modifier.padding(24.dp)) {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.headlineSmall,
                )
                Spacer(modifier = Modifier.height(24.dp))
                ConnectedRadioButton(
                    label = "File Name",
                    selected = selectedSortBy == SortBy.NAME,
                    onClick = { onSelectValue(sortBy = SortBy.NAME) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = VerticalConnectedRadioButtonShape.TopButtonShape
                )
                Spacer(modifier = Modifier.height(4.dp))
                ConnectedRadioButton(
                    label = "Page Count",
                    selected = selectedSortBy == SortBy.PAGES,
                    onClick = { onSelectValue(sortBy = SortBy.PAGES) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = VerticalConnectedRadioButtonShape.MiddleButtonShape
                )
                Spacer(modifier = Modifier.height(4.dp))
                ConnectedRadioButton(
                    label = "Last Modified",
                    selected = selectedSortBy == SortBy.TIME,
                    onClick = { onSelectValue(sortBy = SortBy.TIME) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = VerticalConnectedRadioButtonShape.BottomButtonShape
                )

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ConnectedRadioButton(
                        label = "Ascending",
                        selected = selectedSortOrder == SortOrder.ASCENDING,
                        onClick = { onSelectValue(sortOrder = SortOrder.ASCENDING) },
                        modifier = Modifier.weight(1f),
                        shape = HorizontalConnectedRadioButtonShape.TopButtonShape
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    ConnectedRadioButton(
                        label = "Descending",
                        selected = selectedSortOrder == SortOrder.DESCENDING,
                        onClick = { onSelectValue(sortOrder = SortOrder.DESCENDING) },
                        modifier = Modifier.weight(1f),
                        shape = HorizontalConnectedRadioButtonShape.BottomButtonShape
                    )
                }
            }
        }
    }
}

