package com.gmail.umidsafarov.lifebalance.presentation.ui.uicomponent.defaults

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gmail.umidsafarov.lifebalance.presentation.ui.theme.LifeBalanceTheme

@Composable
fun DefaultPreloader(
    modifier: Modifier = Modifier,
    size: Dp = 50.dp,
    thickness: Dp = 4.dp,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    Box(
        modifier = modifier,
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(size)
                .align(Alignment.Center),
            color = color,
            strokeWidth = thickness,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    LifeBalanceTheme {
        DefaultPreloader(modifier = Modifier.fillMaxSize())
    }
}