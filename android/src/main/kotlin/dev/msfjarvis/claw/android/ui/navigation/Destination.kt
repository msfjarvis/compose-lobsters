/*
 * Copyright © 2024 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.navigation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

sealed interface Destination : Parcelable

@Parcelize @Serializable data object Hottest : Destination

@Parcelize @Serializable data object Newest : Destination

@Parcelize @Serializable data object Saved : Destination

@Parcelize @Serializable data class Comments(val postId: String) : Destination

@Parcelize @Serializable data class User(val username: String) : Destination

@Parcelize @Serializable data object Search : Destination

@Parcelize @Serializable data object Settings : Destination

@Parcelize @Serializable data object AboutLibraries : Destination
