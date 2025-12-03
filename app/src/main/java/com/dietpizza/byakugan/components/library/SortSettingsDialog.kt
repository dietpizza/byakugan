package com.dietpizza.byakugan.components.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
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

    fun onSelectValue(sortBy: SortBy = selectedSortBy, sortOrder: SortOrder = selectedSortOrder) {

        selectedSortBy = sortBy
        selectedSortOrder = sortOrder
        onConfirm(SortSettings(selectedSortBy, selectedSortOrder))
        onDismiss()
    }


    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {},
        title = {
            Text(
                text = "Library Settings",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        },

        text = {
            Column {
                ConnectedRadioButton(
                    label = "Name of file",
                    selected = selectedSortBy == SortBy.NAME,
                    onClick = { onSelectValue(sortBy = SortBy.NAME) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = VerticalConnectedRadioButtonShape.TopButtonShape
                )
                Spacer(modifier = Modifier.height(4.dp))
                ConnectedRadioButton(
                    label = "Number of Pages",
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

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(12.dp))

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
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ConnectedRadioButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(12.dp)
) {
    if (selected) {
        Button(
            onClick,
            modifier,
            shape = shape,
            contentPadding = PaddingValues(vertical = 16.dp),
        ) {
            Text(label)
        }
    } else {
        FilledTonalButton(
            onClick,
            modifier,
            shape = shape,
            contentPadding = PaddingValues(vertical = 16.dp),
        ) {
            Text(label, style = MaterialTheme.typography.labelLarge)
        }
    }
}

object VerticalConnectedRadioButtonShape {
    val TopButtonShape = RoundedCornerShape(
        topStart = 12.dp,
        bottomStart = 4.dp,
        topEnd = 12.dp,
        bottomEnd = 4.dp,
    )
    val MiddleButtonShape = RoundedCornerShape(
        4.dp
    )
    val BottomButtonShape = RoundedCornerShape(
        topStart = 4.dp,
        bottomStart = 12.dp,
        topEnd = 4.dp,
        bottomEnd = 12.dp,
    )
}

object HorizontalConnectedRadioButtonShape {
    val TopButtonShape = RoundedCornerShape(
        topStart = 12.dp,
        bottomStart = 12.dp,
        topEnd = 4.dp,
        bottomEnd = 4.dp,
    )
    val MiddleButtonShape = RoundedCornerShape(
        4.dp
    )
    val BottomButtonShape = RoundedCornerShape(
        topStart = 4.dp,
        bottomStart = 4.dp,
        topEnd = 12.dp,
        bottomEnd = 12.dp,
    )
}
