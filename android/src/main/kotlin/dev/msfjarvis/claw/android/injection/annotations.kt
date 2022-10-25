/*
 * Copyright © 2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.injection

import javax.inject.Qualifier

@Qualifier @Retention(AnnotationRetention.RUNTIME) annotation class DatabaseDispatcher

@Qualifier @Retention(AnnotationRetention.RUNTIME) annotation class MainDispatcher

@Qualifier @Retention(AnnotationRetention.RUNTIME) annotation class IODispatcher
