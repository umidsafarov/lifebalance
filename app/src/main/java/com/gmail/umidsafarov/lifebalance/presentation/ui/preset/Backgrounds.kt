package com.gmail.umidsafarov.lifebalance.presentation.ui.preset

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object Backgrounds {
    private val list = listOf(
        Brush.linearGradient(listOf(Color(0xFF8E9EAB), Color(0xFFEEF2F3))),
        Brush.linearGradient(listOf(Color(0xFFFFEFBA), Color(0xFFFFFFFF))),
        Brush.linearGradient(listOf(Color(0xFFD3CCE3), Color(0xFFE9E4F0))),
        Brush.linearGradient(listOf(Color(0xFFC9D6FF), Color(0xFFE2E2E2))),
        Brush.linearGradient(listOf(Color(0xFFD9A7C7), Color(0xFFFFFCDC))),
        Brush.linearGradient(listOf(Color(0xFFED4264), Color(0xFFFFEDBC))),
        Brush.linearGradient(listOf(Color(0xFF83A4D4), Color(0xFFB6FBFF))),
        Brush.linearGradient(listOf(Color(0xFFB2FEFA), Color(0xFF0ED2F7))),
    )

    fun getList() = list

    fun getByIndex(index: Int?): Brush {
        if (index == null || index < 0 || index >= list.size)
            return list[0]
        return list[index]
    }

    fun getRandom(): Int {
        return list.indices.random()
    }
}