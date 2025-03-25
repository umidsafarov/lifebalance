package com.gmail.umidsafarov.lifebalance.presentation.screen.task.uicomponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gmail.umidsafarov.lifebalance.R
import com.gmail.umidsafarov.lifebalance.presentation.screen.task.TaskContract
import com.gmail.umidsafarov.lifebalance.presentation.ui.theme.LifeBalanceTheme
import com.gmail.umidsafarov.lifebalance.presentation.ui.utils.extensions.toBrush

@Composable
fun TaskDetails(
    state: TaskContract.State,
    sendEvent: (event: TaskContract.UIEvent) -> Unit,
    onChooseColor: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(30.dp))
                .background(state.colorIndex.toBrush())
                .padding(vertical = 15.dp)
                .testTag(stringResource(R.string.compose_test_tag_task_details_container)),
        ) {
            TextField(
                value = state.title.orEmpty(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                onValueChange = { sendEvent(TaskContract.UIEvent.TitleChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
            )
            HorizontalDivider(
                color = Color.White,
                modifier = Modifier.padding(horizontal = 60.dp, vertical = 10.dp),
            )
            TextField(
                value = state.description.orEmpty(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                onValueChange = { sendEvent(TaskContract.UIEvent.DescriptionChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            )
            HorizontalDivider(
                color = Color.White,
                modifier = Modifier.padding(horizontal = 60.dp, vertical = 10.dp),
            )
            TextButton(
                onClick = {
                    onChooseColor?.invoke()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = stringResource(R.string.button_change_color),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    LifeBalanceTheme {
        TaskDetails(
            state = TaskContract.State(
                title = "Sample task title",
                description = "Some description",
                colorIndex = 0,
            ),
            sendEvent = { },
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        )
    }
}