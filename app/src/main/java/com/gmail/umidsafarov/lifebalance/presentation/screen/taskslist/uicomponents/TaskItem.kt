package com.gmail.umidsafarov.lifebalance.presentation.screen.taskslist.uicomponents

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gmail.umidsafarov.lifebalance.presentation.ui.theme.DarkOutline
import com.gmail.umidsafarov.lifebalance.presentation.ui.theme.LifeBalanceTheme
import com.gmail.umidsafarov.lifebalance.presentation.ui.theme.LightOutline
import com.gmail.umidsafarov.lifebalance.presentation.ui.utils.extensions.toBrush

@Composable
fun TaskItem(
    title: String,
    colorIndex: Int?,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    val textWeight = remember(isSelected) {
        if (isSelected) FontWeight.Bold else FontWeight.Normal
    }

    val outlineColor = remember(isSelected) {
        if (isSelected)
            DarkOutline
        else
            LightOutline
    }
    val outlineWidth = remember(isSelected) {
        if (isSelected)
            1f.dp
        else
            .5f.dp
    }

    OutlinedCard(
        modifier = modifier,
        border = BorderStroke(outlineWidth, outlineColor)
    ) {
        Row(
            modifier = Modifier
                .background(colorIndex.toBrush())
                .padding(vertical = 15.dp, horizontal = 30.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = textWeight,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    LifeBalanceTheme {
        TaskItem(
            title = "Example title",
            colorIndex = 0,
            isSelected = false,
            modifier = Modifier
                .size(150.dp, 200.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewSelected() {
    LifeBalanceTheme {
        TaskItem(
            title = "Example title",
            colorIndex = 1,
            isSelected = true,
            modifier = Modifier
                .size(150.dp, 200.dp),
        )
    }
}