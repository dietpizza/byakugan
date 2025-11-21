package com.dietpizza.byakugan.components.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

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
