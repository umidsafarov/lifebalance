package com.gmail.umidsafarov.lifebalance.presentation.screen.task

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextInputSelection
import androidx.compose.ui.text.TextRange
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import com.gmail.umidsafarov.lifebalance.R
import com.gmail.umidsafarov.lifebalance.common.model.OperationError
import com.gmail.umidsafarov.lifebalance.extension.hasBackground
import com.gmail.umidsafarov.lifebalance.presentation.ui.preset.Backgrounds
import com.gmail.umidsafarov.lifebalance.presentation.ui.theme.LifeBalanceTheme
import com.google.common.truth.Truth
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@SmallTest
@RunWith(AndroidJUnit4::class)
class TaskScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var context: Context

    private lateinit var testState: MutableState<TaskContract.State>
    private lateinit var eventsFlow: MutableSharedFlow<TaskContract.Event>
    private lateinit var uiEventsList: MutableList<TaskContract.UIEvent>
    private lateinit var navigateBackEvents: MutableList<Long?>

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext

        testState = mutableStateOf(
            TaskContract.State(
                title = "Test Task",
                description = "Test Description",
                colorIndex = 1,
                isLoading = false,
            )
        )

        eventsFlow = MutableSharedFlow()
        uiEventsList = mutableListOf()
        navigateBackEvents = mutableListOf()

        composeTestRule.setContent {
            LifeBalanceTheme {
                TaskScreen(
                    state = testState.value,
                    events = eventsFlow,
                    sendEvent = uiEventsList::add,
                    onNavigateBack = {
                        navigateBackEvents.add(it)
                    }
                )
            }
        }
    }

    @Test
    fun init_whenTaskIsSet_viewsExist() {
        composeTestRule.onNodeWithText(testState.value.title.orEmpty()).assertExists()
        composeTestRule.onNodeWithText(testState.value.description.orEmpty()).assertExists()
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.content_description_save_task))
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.content_description_delete_task))
    }

    @Test
    fun init_whenTaskIsSet_chosenBackgroundDisplayed() {
        val node =
            composeTestRule.onNodeWithTag(context.getString(R.string.compose_test_tag_task_details_container))
                .fetchSemanticsNode()

        Truth.assertThat(node.hasBackground(Backgrounds.getByIndex(testState.value.colorIndex)))
            .isTrue()
    }

    @Test
    fun loadingState_showsPreloader() {
        testState.value = testState.value.copy(isLoading = true)

        composeTestRule.onNodeWithTag(context.getString(R.string.compose_test_tag_preloader))
            .assertExists()
    }

    @Test
    fun clickSave_saveEventSent() {
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.content_description_save_task))
            .performClick()

        Truth.assertThat(uiEventsList).hasSize(1)
        Truth.assertThat(uiEventsList[0]).isInstanceOf(TaskContract.UIEvent.Save::class.java)
    }

    @Test
    fun clickDelete_deleteEventSent() {
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.content_description_delete_task))
            .performClick()

        Truth.assertThat(uiEventsList).hasSize(1)
        Truth.assertThat(uiEventsList[0]).isInstanceOf(TaskContract.UIEvent.Remove::class.java)
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun typeTitle_eventSent() {
        val additionalText = "additional text"
        composeTestRule.onNodeWithText(testState.value.title.orEmpty())
            .performTextInputSelection(TextRange(testState.value.title.orEmpty().length))
        composeTestRule.onNodeWithText(testState.value.title.orEmpty())
            .performTextInput(additionalText)

        Truth.assertThat(uiEventsList).hasSize(1)
        Truth.assertThat(uiEventsList[0])
            .isInstanceOf(TaskContract.UIEvent.TitleChanged::class.java)
        Truth.assertThat((uiEventsList[0] as TaskContract.UIEvent.TitleChanged).value)
            .isEqualTo("${testState.value.title}$additionalText")
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun typeDescription_eventSent() {
        val additionalText = "additional text"
        composeTestRule.onNodeWithText(testState.value.description.orEmpty())
            .performTextInputSelection(TextRange(testState.value.description.orEmpty().length))
        composeTestRule.onNodeWithText(testState.value.description.orEmpty())
            .performTextInput(additionalText)

        Truth.assertThat(uiEventsList).hasSize(1)
        Truth.assertThat(uiEventsList[0])
            .isInstanceOf(TaskContract.UIEvent.DescriptionChanged::class.java)
        Truth.assertThat((uiEventsList[0] as TaskContract.UIEvent.DescriptionChanged).value)
            .isEqualTo("${testState.value.description}$additionalText")
    }

    @Test
    fun clickChangeColor_ShowsColorSelectionDialog() {
        composeTestRule.onNodeWithText(context.getString(R.string.button_change_color))
            .performClick()
        composeTestRule.onNodeWithText(context.getString(R.string.title_color_choose))
            .assertExists()
    }

    @Test
    fun colorChoose_eventSent() {
        composeTestRule.onNodeWithText(context.getString(R.string.button_change_color))
            .performClick()

        composeTestRule.onNodeWithText(context.getString(R.string.title_color_choose))
            .assertExists()

        composeTestRule.onAllNodesWithTag(context.getString(R.string.compose_test_tag_choose_color_item))[1].performClick()

        Truth.assertThat(uiEventsList).hasSize(1)
        Truth.assertThat(uiEventsList[0])
            .isInstanceOf(TaskContract.UIEvent.ColorChanged::class.java)
    }

    @Test
    fun closeButtonClick_closeDialog() {
        composeTestRule.onNodeWithText(context.getString(R.string.button_change_color))
            .performClick()

        composeTestRule.onNodeWithText(context.getString(R.string.title_color_choose))
            .assertExists()

        composeTestRule.onNodeWithText(context.getString(R.string.button_close)).performClick()


        composeTestRule.onNodeWithText(context.getString(R.string.title_color_choose))
            .assertDoesNotExist()
    }

    @Test
    fun sendNavigateBackEvent_navigateBackCalled() = runTest {
        val resultId = 123L
        eventsFlow.emit(TaskContract.Event.NavigateBack(resultId))

        Truth.assertThat(navigateBackEvents).hasSize(1)
        Truth.assertThat(navigateBackEvents[0]).isEqualTo(resultId)
    }

    @Test
    fun sendErrorEvent_snackbarShown() = runTest {
        val errorText = "Test Error Text"
        eventsFlow.emit(TaskContract.Event.Error(OperationError.Unspecified(errorText)))

        composeTestRule.onNode(
            hasText(errorText),
            useUnmergedTree = true
        ).assertIsDisplayed()
    }
}