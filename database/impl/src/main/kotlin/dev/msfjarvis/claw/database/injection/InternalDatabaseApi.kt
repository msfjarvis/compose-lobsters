/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.database.injection

import dev.msfjarvis.claw.database.LobstersDatabase
import dev.zacsweers.metro.Qualifier

/**
 * Neato workaround for not allowing the [LobstersDatabase] type to be used directly by modules that
 * depend on this one, as we prefer them to use the specific types from [QueriesModule] instead. A
 * [Qualifier] applied to the [LobstersDatabase] type makes all injection sites require it as well,
 * and marking the annotation class' visibility as `internal` prevents it from being used outside
 * this module.
 */
@Qualifier @Retention(AnnotationRetention.RUNTIME) internal annotation class InternalDatabaseApi
