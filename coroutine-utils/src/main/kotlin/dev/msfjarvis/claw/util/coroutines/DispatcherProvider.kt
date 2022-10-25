/*
 * Copyright © 2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
@file:Suppress("InjectDispatcher") // False-positive

package dev.msfjarvis.claw.util.coroutines

import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi

/** Interface to allow abstracting individual [CoroutineDispatcher]s out of class dependencies. */
@OptIn(ExperimentalCoroutinesApi::class)
interface DispatcherProvider {

  fun main(): CoroutineDispatcher = Dispatchers.Main
  fun default(): CoroutineDispatcher = Dispatchers.Default
  fun io(): CoroutineDispatcher = Dispatchers.IO
  fun unconfined(): CoroutineDispatcher = Dispatchers.Unconfined
  fun database(): CoroutineDispatcher = Dispatchers.IO.limitedParallelism(1)
}

/** Concrete type for [DispatcherProvider] with all the defaults from the class. */
class DefaultDispatcherProvider @Inject constructor() : DispatcherProvider
