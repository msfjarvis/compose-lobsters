/*
 * Copyright © 2024 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.navigation

import kotlinx.serialization.Serializable

sealed interface Destination

@Serializable data object Hottest : Destination

@Serializable data object Newest : Destination

@Serializable data object Saved : Destination

@Serializable data class Comments(val postId: String) : Destination

@Serializable data class User(val username: String) : Destination

@Serializable data object Search : Destination

@Serializable data object Settings : Destination

@Serializable data object AboutLibraries : Destination
