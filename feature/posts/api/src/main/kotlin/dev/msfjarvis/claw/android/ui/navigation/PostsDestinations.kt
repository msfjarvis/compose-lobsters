/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.navigation

import android.os.Parcelable
import androidx.navigation3.runtime.NavKey
import dev.msfjarvis.claw.navigation.api.TopLevelDestination
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize @Serializable data object Hottest : NavKey, TopLevelDestination, Parcelable

@Parcelize @Serializable data object Newest : NavKey, TopLevelDestination, Parcelable

@Parcelize @Serializable data object Saved : NavKey, TopLevelDestination, Parcelable
