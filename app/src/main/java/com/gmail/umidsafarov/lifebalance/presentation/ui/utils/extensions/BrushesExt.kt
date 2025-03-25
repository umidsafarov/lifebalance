package com.gmail.umidsafarov.lifebalance.presentation.ui.utils.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Brush
import com.gmail.umidsafarov.lifebalance.presentation.ui.preset.Backgrounds

@Composable
fun Int?.toBrush(): Brush {
    return remember(this) { Backgrounds.getByIndex(this) }
}
