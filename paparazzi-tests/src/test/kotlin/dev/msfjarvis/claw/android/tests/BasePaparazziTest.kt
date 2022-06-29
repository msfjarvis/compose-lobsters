package dev.msfjarvis.claw.android.tests

import app.cash.paparazzi.Paparazzi
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import org.junit.Rule
import org.junit.runner.RunWith

/** Base class for Paparazzi based tests that handles parameterization based on [Theme]. */
@RunWith(TestParameterInjector::class)
abstract class BasePaparazziTest {
  @get:Rule val paparazzi = Paparazzi()
  @TestParameter lateinit var theme: Theme
}
