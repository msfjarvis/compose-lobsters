/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
@file:Suppress("RawDispatchersUse") // False-positive

package dev.msfjarvis.claw.core.coroutines

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.Qualifier
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
@Target(
  AnnotationTarget.VALUE_PARAMETER,
  AnnotationTarget.LOCAL_VARIABLE,
  AnnotationTarget.FUNCTION,
)
annotation class DatabaseReadDispatcher

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
@Target(
  AnnotationTarget.VALUE_PARAMETER,
  AnnotationTarget.LOCAL_VARIABLE,
  AnnotationTarget.FUNCTION,
)
annotation class DatabaseWriteDispatcher

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
@Target(
  AnnotationTarget.VALUE_PARAMETER,
  AnnotationTarget.LOCAL_VARIABLE,
  AnnotationTarget.FUNCTION,
)
annotation class MainDispatcher

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
@Target(
  AnnotationTarget.VALUE_PARAMETER,
  AnnotationTarget.LOCAL_VARIABLE,
  AnnotationTarget.FUNCTION,
)
annotation class IODispatcher

@Qualifier @Retention(AnnotationRetention.RUNTIME) annotation class DefaultDispatcher

@BindingContainer
@ContributesTo(AppScope::class)
object DispatcherProvider {

  @[Provides MainDispatcher]
  fun main(): CoroutineDispatcher = Dispatchers.Main

  @[Provides DefaultDispatcher]
  fun default(): CoroutineDispatcher = Dispatchers.Default

  @[Provides IODispatcher]
  fun io(): CoroutineDispatcher = Dispatchers.IO

  @[Provides DatabaseReadDispatcher]
  fun databaseRead(): CoroutineDispatcher =
    Dispatchers.IO.limitedParallelism(4, name = "DatabaseRead")

  @[Provides DatabaseWriteDispatcher]
  fun databaseWrite(): CoroutineDispatcher =
    Dispatchers.IO.limitedParallelism(1, name = "DatabaseWrite")
}
