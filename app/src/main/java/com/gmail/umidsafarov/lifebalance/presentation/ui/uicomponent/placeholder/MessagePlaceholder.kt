package com.gmail.umidsafarov.lifebalance.presentation.ui.uicomponent.placeholder

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.gmail.umidsafarov.lifebalance.R
import com.gmail.umidsafarov.lifebalance.presentation.ui.theme.LifeBalanceTheme


@Composable
fun MessagePlaceholder(
    message: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Black,
    onClick: (() -> Unit)? = null,
) {
    Box(modifier = modifier) {
        Text(
            text = message,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .align(Alignment.Center)
                .clickable { onClick?.invoke() },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    LifeBalanceTheme {
        MessagePlaceholder(
            message = stringResource(R.string.message_empty_items),
            color = Color.Gray,
            modifier = Modifier.fillMaxSize(),
        )
    }
}