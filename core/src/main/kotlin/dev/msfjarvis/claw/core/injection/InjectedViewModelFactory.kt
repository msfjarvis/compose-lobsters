/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.core.injection

import androidx.lifecycle.ViewModel
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provider
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.MetroViewModelFactory
import dev.zacsweers.metrox.viewmodel.ViewModelAssistedFactory
import kotlin.reflect.KClass

@Inject
@ContributesBinding(AppScope::class)
@SingleIn(AppScope::class)
class InjectedViewModelFactory(
  override val viewModelProviders: Map<KClass<out ViewModel>, Provider<ViewModel>>,
  override val assistedFactoryProviders:
    Map<KClass<out ViewModel>, Provider<ViewModelAssistedFactory>>,
  override val manualAssistedFactoryProviders:
    Map<KClass<out ManualViewModelAssistedFactory>, Provider<ManualViewModelAssistedFactory>>,
) : MetroViewModelFactory()
