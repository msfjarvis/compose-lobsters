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
