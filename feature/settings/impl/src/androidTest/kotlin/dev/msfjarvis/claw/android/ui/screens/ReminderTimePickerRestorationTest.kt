/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.screens

import android.view.View
import android.widget.TimePicker
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.msfjarvis.claw.common.theme.LobstersTheme
import dev.msfjarvis.claw.feature.settings.impl.SettingsAppConfig
import kotlinx.datetime.LocalTime
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReminderTimePickerRestorationTest {
  @get:Rule val compose = createComposeRule()

  @Test
  fun editedReminderTimeSurvivesSavedStateRestoreAndCanBeConfirmed() {
    val committedTimes = mutableListOf<LocalTime>()
    val restoration = StateRestorationTester(compose)
    restoration.setContent {
      LobstersTheme {
        SettingsScreen(
          openLibrariesScreen = {},
          openRepository = {},
          openTagFiltering = {},
          openLoginScreen = {},
          onLogout = {},
          isLoggedIn = false,
          username = null,
          snackbarHostState = SnackbarHostState(),
          importPosts = { Result.success(Unit) },
          exportPostsAsJson = {},
          contentPadding = PaddingValues(),
          savedPostsCount = 0,
          isDailySavedPostReminderEnabled = true,
          dailySavedPostReminderTime = LocalTime(9, 0),
          onDailySavedPostReminderEnabledChange = {},
          onDailySavedPostReminderTimeChange = committedTimes::add,
          settingsAppConfig = SettingsAppConfig("1.0", 1) {},
        )
      }
    }

    compose.onNodeWithText("Reminder time").performClick()
    onView(timePicker()).inRoot(isDialog()).perform(setTime(14, 37))

    restoration.emulateSavedInstanceStateRestore()

    onView(timePicker()).inRoot(isDialog()).check(matches(hasTime(14, 37)))
    onView(withId(android.R.id.button1)).inRoot(isDialog()).perform(click())
    compose.runOnIdle { assertEquals(listOf(LocalTime(14, 37)), committedTimes) }
  }

  @Test
  fun cancellingEditedReminderTimeDoesNotCommit() {
    val committedTimes = mutableListOf<LocalTime>()
    compose.setContent {
      LobstersTheme {
        SettingsScreen(
          openLibrariesScreen = {},
          openRepository = {},
          openTagFiltering = {},
          openLoginScreen = {},
          onLogout = {},
          isLoggedIn = false,
          username = null,
          snackbarHostState = SnackbarHostState(),
          importPosts = { Result.success(Unit) },
          exportPostsAsJson = {},
          contentPadding = PaddingValues(),
          savedPostsCount = 0,
          isDailySavedPostReminderEnabled = true,
          dailySavedPostReminderTime = LocalTime(9, 0),
          onDailySavedPostReminderEnabledChange = {},
          onDailySavedPostReminderTimeChange = committedTimes::add,
          settingsAppConfig = SettingsAppConfig("1.0", 1) {},
        )
      }
    }

    compose.onNodeWithText("Reminder time").performClick()
    onView(timePicker()).inRoot(isDialog()).perform(setTime(14, 37))
    onView(withId(android.R.id.button2)).inRoot(isDialog()).perform(click())

    compose.runOnIdle { assertEquals(emptyList<LocalTime>(), committedTimes) }
  }

  private fun setTime(hour: Int, minute: Int): ViewAction =
    object : ViewAction {
      override fun getConstraints(): Matcher<View> = timePicker()

      override fun getDescription(): String = "Set time to $hour:$minute"

      override fun perform(uiController: UiController, view: View) {
        val picker = view as TimePicker
        picker.hour = hour
        picker.minute = minute
      }
    }

  private fun timePicker(): Matcher<View> = allOf(isAssignableFrom(TimePicker::class.java))

  private fun hasTime(hour: Int, minute: Int): Matcher<View> =
    object : TypeSafeMatcher<View>() {
      override fun describeTo(description: Description) {
        description.appendText("TimePicker with time $hour:$minute")
      }

      override fun matchesSafely(view: View): Boolean =
        view is TimePicker && view.hour == hour && view.minute == minute
    }
}
