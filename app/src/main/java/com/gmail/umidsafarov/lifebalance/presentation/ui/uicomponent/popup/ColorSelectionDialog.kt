package com.gmail.umidsafarov.lifebalance.presentation.ui.uicomponent.popup

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gmail.umidsafarov.lifebalance.R
import com.gmail.umidsafarov.lifebalance.presentation.ui.preset.Backgrounds
import com.gmail.umidsafarov.lifebalance.presentation.ui.theme.DarkOutline
import com.gmail.umidsafarov.lifebalance.presentation.ui.theme.LifeBalanceTheme
import com.gmail.umidsafarov.lifebalance.presentation.ui.theme.LightOutline

@Composable
fun ColorSelectionDialog(
    currentColorIndex: Int?,
    onColorSelected: ((colorIndex: Int) -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
) {
    val colorsList = remember { Backgrounds.getList() }

    AlertDialog(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        title = {
            Text(
                text = stringResource(R.string.title_color_choose),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                LazyColumn {
                    itemsIndexed(colorsList) { index, brush ->
                        val outlineColor = remember(currentColorIndex) {
                            if (currentColorIndex == index)
                                DarkOutline
                            else
                                LightOutline
                        }
                        val outlineWidth = remember(currentColorIndex) {
                            if (currentColorIndex == index)
                                1f.dp
                            else
                                .5f.dp
                        }

                        OutlinedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp).testTag(stringResource(R.string.compose_test_tag_choose_color_item)),
                            onClick = {
                                onColorSelected?.invoke(index)
                            },
                            border = BorderStroke(outlineWidth, outlineColor),
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(brush)
                            )
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onDismiss?.invoke() },
            ) {
                Text(
                    text = stringResource(R.string.button_close),
                    fontWeight = FontWeight.Bold,
                )
            }
        },
        onDismissRequest = {
            onDismiss?.invoke()
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    LifeBalanceTheme {
        ColorSelectionDialog(
            currentColorIndex = 2,
        )
    }
}