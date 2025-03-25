package com.gmail.umidsafarov.lifebalance.presentation.navigation

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.navigation.toRoute
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.gmail.umidsafarov.lifebalance.R
import com.gmail.umidsafarov.lifebalance.presentation.platform.HiltEntryPointActivity
import com.gmail.umidsafarov.lifebalance.presentation.ui.theme.LifeBalanceTheme
import com.google.common.truth.Truth
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class NavigationTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createAndroidComposeRule<HiltEntryPointActivity>()

    private lateinit var context: Context

    private lateinit var navController: TestNavHostController

    @Before
    fun setUp() {
        hiltRule.inject()

        context = InstrumentationRegistry.getInstrumentation().targetContext

        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }

            LifeBalanceTheme {
                Navigation(navController)
            }
        }
    }

    @Test
    fun init_startDestinationIsCorrect() {
        Truth.assertThat(navController.getCurrentRoute<Routes.TaskRoute>()).isNull()
        Truth.assertThat(navController.getCurrentRoute<Routes.TasksListRoute>()).isNotNull()
    }

    @Test
    fun clickCreateTask_navigatesToTaskDetails() {
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.content_description_add_task))
            .performClick()

        Truth.assertThat(navController.getCurrentRoute<Routes.TaskRoute>()).isNotNull()
    }

    @Test
    fun clickSave_whenOnTaskDetails_navigatesBack() {
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.content_description_add_task))
            .performClick()

        Truth.assertThat(navController.getCurrentRoute<Routes.TaskRoute>()).isNotNull()

        composeTestRule.onNodeWithContentDescription(context.getString(R.string.content_description_save_task))
            .performClick()

        Truth.assertThat(navController.getCurrentRoute<Routes.TasksListRoute>()).isNotNull()
    }

    @Test
    fun clickBack_whenOnTaskDetails_navigatesBack() {
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.content_description_add_task))
            .performClick()

        Truth.assertThat(navController.getCurrentRoute<Routes.TaskRoute>()).isNotNull()

        Espresso.pressBack()

        Truth.assertThat(navController.getCurrentRoute<Routes.TasksListRoute>()).isNotNull()
    }

    private inline fun <reified T : Any> TestNavHostController.getCurrentRoute(): T? {
        if (this.currentBackStackEntry?.destination?.hasRoute(T::class) == true) {
            return this.currentBackStackEntry?.toRoute<T>()
        }
        return null
    }
}