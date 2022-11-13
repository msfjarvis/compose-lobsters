/*
 * Copyright © 2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.injection

object Components {

  @PublishedApi
  @Suppress("ObjectPropertyName", "ObjectPropertyNaming")
  internal val _components = mutableSetOf<Any>()

  fun add(component: Any) {
    _components.add(component)
  }

  inline fun <reified T> get(): T = _components.filterIsInstance<T>().single()
}
