package com.gmail.umidsafarov.lifebalance.presentation.screen.taskslist

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import com.gmail.umidsafarov.lifebalance.R
import com.gmail.umidsafarov.lifebalance.domain.model.Task
import com.gmail.umidsafarov.lifebalance.presentation.screen.taskslist.state.TaskState
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
class TasksListScreenTest {


    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var context: Context

    private lateinit var testState: MutableState<TasksListContract.State>
    private lateinit var eventsFlow: MutableSharedFlow<TasksListContract.Event>
    private lateinit var uiEventsList: MutableList<TasksListContract.UIEvent>
    private lateinit var navigateToTaskEvents: MutableList<Long?>

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext

        testState = mutableStateOf(
            TasksListContract.State(
                tasks = listOf(
                    TaskState(
                        data = Task(
                            id = 1,
                            title = "Test Task",
                            description = "Test Description",
                            colorIndex = 1,
                        ),
                        isSelected = mutableStateOf(false),
                    ),
                    TaskState(
                        data = Task(
                            id = 2,
                            title = "Test Task 2",
                            description = "Test Description 2",
                            colorIndex = 2,
                        ),
                        isSelected = mutableStateOf(true),
                    )
                ),
                isLoading = false,
            ),
        )

        eventsFlow = MutableSharedFlow()
        uiEventsList = mutableListOf()
        navigateToTaskEvents = mutableListOf()

        composeTestRule.setContent {
            LifeBalanceTheme {
                TasksListScreen(
                    state = testState.value,
                    events = eventsFlow,
                    sendEvent = uiEventsList::add,
                    onNavigateToTask = navigateToTaskEvents::add,
                )
            }
        }
    }

    @Test
    fun stateTasksSetAndLoadingFalse_viewsExist() {
        Truth.assertThat(testState.value.tasks).hasSize(2)
        composeTestRule.onNodeWithText(testState.value.tasks[0].data.title.orEmpty()).assertExists()
        composeTestRule.onNodeWithText(testState.value.tasks[1].data.title.orEmpty()).assertExists()
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.content_description_add_task))
            .assertExists()

        composeTestRule.onNodeWithTag(context.getString(R.string.compose_test_tag_preloader))
            .assertDoesNotExist()
        composeTestRule.onNodeWithTag(context.getString(R.string.compose_test_tag_item_preloader))
            .assertDoesNotExist()
    }

    @Test
    fun stateTasksSetAndLoadingTrue_viewsExist() {
        testState.value = testState.value.copy(isLoading = true)

        Truth.assertThat(testState.value.tasks).hasSize(2)
        composeTestRule.onNodeWithText(testState.value.tasks[0].data.title.orEmpty()).assertExists()
        composeTestRule.onNodeWithText(testState.value.tasks[1].data.title.orEmpty()).assertExists()
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.content_description_add_task))
            .assertExists()

        composeTestRule.onNodeWithTag(context.getString(R.string.compose_test_tag_preloader))
            .assertDoesNotExist()
        composeTestRule.onNodeWithTag(context.getString(R.string.compose_test_tag_item_preloader))
            .assertExists()
    }

    @Test
    fun stateNoTasksAndLoadingFalse_viewsExist() {
        testState.value = TasksListContract.State(
            tasks = listOf(),
            isLoading = false,
        )
        composeTestRule.onNodeWithText(context.getString(R.string.message_empty_items))
            .assertExists()
        composeTestRule.onNodeWithTag(context.getString(R.string.compose_test_tag_preloader))
            .assertDoesNotExist()
        composeTestRule.onNodeWithTag(context.getString(R.string.compose_test_tag_item_preloader))
            .assertDoesNotExist()
    }

    @Test
    fun stateNoTasksAndLoadingTrue_viewsExist() {
        testState.value = TasksListContract.State(
            tasks = listOf(),
            isLoading = true,
        )

        composeTestRule.onNodeWithText(context.getString(R.string.message_empty_items))
            .assertDoesNotExist()
        composeTestRule.onNodeWithTag(context.getString(R.string.compose_test_tag_preloader))
            .assertExists()
        composeTestRule.onNodeWithTag(context.getString(R.string.compose_test_tag_item_preloader))
            .assertDoesNotExist()
    }

    @Test
    fun clickOnTask_eventTriggered() {
        composeTestRule.onNodeWithText(testState.value.tasks[0].data.title.orEmpty())
            .performClick()
        val events = uiEventsList.filterIsInstance<TasksListContract.UIEvent.TaskChosen>()

        Truth.assertThat(events).hasSize(1)
        Truth.assertThat((events[0]).task.data.id)
            .isEqualTo(testState.value.tasks[0].data.id)
    }

    @Test
    fun longClickOnTask_eventTriggered() {
        composeTestRule.onNodeWithText(testState.value.tasks[0].data.title.orEmpty())
            .performTouchInput {
                longClick()
            }
        val events = uiEventsList.filterIsInstance<TasksListContract.UIEvent.TaskSelected>()

        Truth.assertThat(events).hasSize(1)
        Truth.assertThat((events[0]).task.data.id)
            .isEqualTo(testState.value.tasks[0].data.id)
    }

    @Test
    fun clickOnCreateTask_eventTriggered() {
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.content_description_add_task))
            .performClick()
        val events = uiEventsList.filterIsInstance<TasksListContract.UIEvent.CreateTask>()

        Truth.assertThat(events).hasSize(1)
    }

    @Test
    fun sendNavigateToTaskEvent_eventTriggered() = runTest {
        eventsFlow.emit(TasksListContract.Event.NavigateToTask(1))

        Truth.assertThat(navigateToTaskEvents).hasSize(1)
        Truth.assertThat(navigateToTaskEvents[0]).isEqualTo(1)
    }

}