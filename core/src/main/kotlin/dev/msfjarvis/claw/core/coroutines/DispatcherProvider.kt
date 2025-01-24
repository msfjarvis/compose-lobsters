/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
@file:Suppress("RawDispatchersUse") // False-positive

package dev.msfjarvis.claw.core.coroutines

import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/** Interface to allow abstracting individual [CoroutineDispatcher]s out of class dependencies. */
interface DispatcherProvider {

  fun main(): CoroutineDispatcher = Dispatchers.Main

  fun default(): CoroutineDispatcher = Dispatchers.Default

  fun io(): CoroutineDispatcher = Dispatchers.IO

  fun database(): CoroutineDispatcher =
    Dispatchers.IO.limitedParallelism(1, name = "DatabaseDispatcher")
}

/** Concrete type for [DispatcherProvider] with all the defaults from the class. */
class DefaultDispatcherProvider @Inject constructor() : DispatcherProvider
