/*
 * Copyright © 2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.injection.scopes

import javax.inject.Scope
import kotlin.reflect.KClass

@Scope @Retention(AnnotationRetention.RUNTIME) annotation class SingleIn(val clazz: KClass<*>)
