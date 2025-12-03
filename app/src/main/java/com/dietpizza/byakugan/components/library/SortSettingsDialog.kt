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
import androidx.compose.material3.ButtonDefaults
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
                SortOption(
                    label = "Name of file",
                    selected = selectedSortBy == SortBy.NAME,
                    onClick = { onSelectValue(sortBy = SortBy.NAME) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = VerticalConnectedButtonsShape.TopButtonShape
                )
                SortOption(
                    label = "Number of Pages",
                    selected = selectedSortBy == SortBy.PAGES,
                    onClick = { onSelectValue(sortBy = SortBy.PAGES) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = VerticalConnectedButtonsShape.MiddleButtonShape
                )
                SortOption(
                    label = "Last Modified",
                    selected = selectedSortBy == SortBy.TIME,
                    onClick = { onSelectValue(sortBy = SortBy.TIME) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = VerticalConnectedButtonsShape.BottomButtonShape
                )

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SortOption(
                        label = "Ascending",
                        selected = selectedSortOrder == SortOrder.ASCENDING,
                        onClick = { onSelectValue(sortOrder = SortOrder.ASCENDING) },
                        modifier = Modifier.weight(1f),
                        shape = HorizontalConnectedButtonsShape.TopButtonShape
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    SortOption(
                        label = "Descending",
                        selected = selectedSortOrder == SortOrder.DESCENDING,
                        onClick = { onSelectValue(sortOrder = SortOrder.DESCENDING) },
                        modifier = Modifier.weight(1f),
                        shape = HorizontalConnectedButtonsShape.BottomButtonShape
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SortOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(12.dp)
) {

    val labelColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onPrimaryContainer
    }

    Button(
        onClick,
        modifier,
        shape = shape,
        contentPadding = PaddingValues(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = labelColor
        )
    ) {
        Text(label)
    }
}


object VerticalConnectedButtonsShape {
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

object HorizontalConnectedButtonsShape {
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
