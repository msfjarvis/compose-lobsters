package dev.msfjarvis.lobsters

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import dev.msfjarvis.lobsters.ui.main.LobstersBottomNav
import dev.msfjarvis.lobsters.ui.navigation.Destination
import dev.msfjarvis.lobsters.ui.theme.LobstersTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class BottomNavigationLayoutTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    composeTestRule.setContent {
      LobstersTheme {
        var mutableDestination by remember { mutableStateOf(Destination.startDestination) }

        LobstersBottomNav(
          currentDestination = mutableDestination,
          navigateToDestination = { mutableDestination = it },
          jumpToIndex = {}
        )
      }
    }
  }

  @Test
  fun bottomNavItemCountTest() {
    // Test to make sure total items are equal to enum objects present in Destination
    composeTestRule.onNodeWithTag("LobstersBottomNav")
      .assertExists()
      .assertIsDisplayed()
      .onChildren()
      .assertCountEquals(Destination.values().size)
  }

  @Test
  fun bottomNavItemTest() {
    // Check hottest BottomNavItem is rendered correctly
    composeTestRule.onNodeWithTag("LobstersBottomNav")
      .assertExists()
      .assertIsDisplayed()
      .onChildAt(0)
      .assertTextEquals("Hottest")
      .assertContentDescriptionEquals("Hottest")
      .assertHasClickAction()

    // Check saved BottomNavItem is rendered correctly
    composeTestRule.onNodeWithTag("LobstersBottomNav")
      .assertExists()
      .assertIsDisplayed()
      .onChildAt(1)
      .assertTextEquals("Saved")
      .assertContentDescriptionEquals("Saved")
      .assertHasClickAction()
  }

  @Test
  fun bottomNavItemSelectedTest() {
    // Check hottest BottomNav item is selected
    composeTestRule.onNodeWithTag("LobstersBottomNav")
      .assertExists()
      .assertIsDisplayed()
      .onChildAt(0)
      .assertIsSelected()
      .assertTextEquals("Hottest")

    // Check saved BottomNav item is not selected
    composeTestRule.onNodeWithTag("LobstersBottomNav")
      .assertExists()
      .assertIsDisplayed()
      .onChildAt(1)
      .assertIsNotSelected()

    // Select the saved BottomNav item
    composeTestRule.onNodeWithTag("LobstersBottomNav")
      .onChildAt(1)
      .performClick()

    // Check hottest BottomNav item is not selected
    composeTestRule.onNodeWithTag("LobstersBottomNav")
      .assertExists()
      .assertIsDisplayed()
      .onChildAt(0)
      .assertIsNotSelected()

    // Check saved BottomNav item is selected
    composeTestRule.onNodeWithTag("LobstersBottomNav")
      .assertExists()
      .assertIsDisplayed()
      .onChildAt(1)
      .assertIsSelected()
      .assertTextEquals("Saved")
  }
}
