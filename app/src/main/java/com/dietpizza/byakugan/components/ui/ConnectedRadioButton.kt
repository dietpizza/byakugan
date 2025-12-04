package com.dietpizza.byakugan.components.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ConnectedRadioButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(12.dp)
) {

    val buttonColors = if (selected) {
        ButtonDefaults.buttonColors()
    } else {
        ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            contentColor = MaterialTheme.colorScheme.onSurface,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    val onButtonClick = {
        if (!selected) onClick()
    }

    Button(
        onClick = onButtonClick,
        modifier = modifier,
        shape = shape,
        colors = buttonColors,
        contentPadding = PaddingValues(vertical = 16.dp),
    ) {
        Text(label, style = MaterialTheme.typography.labelLarge)
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
