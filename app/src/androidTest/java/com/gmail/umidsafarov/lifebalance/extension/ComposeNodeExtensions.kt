package com.gmail.umidsafarov.lifebalance.extension

import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.semantics.SemanticsNode

fun SemanticsNode.hasBackground(brush: Brush): Boolean {
    return this.layoutInfo.getModifierInfo().filter { modifierInfo ->
        modifierInfo.modifier == Modifier.background(brush)
    }.size == 1
}