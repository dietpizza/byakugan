package com.dietpizza.byakugan.components.ui

import androidx.annotation.DrawableRes
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RoundIconButton(@DrawableRes res: Int, onClick: () -> Unit) {
    FilledTonalIconButton(
        onClick,
        modifier = Modifier,
        shapes = IconButtonDefaults.shapes()
    ) {
        Icon(
            painter = painterResource(id = res),
            contentDescription = "Round Icon Button",
        )
    }
}
