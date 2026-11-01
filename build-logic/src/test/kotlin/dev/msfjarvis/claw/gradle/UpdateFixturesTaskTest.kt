/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.gradle

import kotlin.test.Test
import kotlin.test.assertFailsWith

class UpdateFixturesTaskTest {
  @Test
  fun acceptsFilename() {
    validateFixtureFilename("post.html")
  }

  @Test
  fun rejectsPathTraversal() {
    assertFailsWith<IllegalArgumentException> { validateFixtureFilename("../outside.html") }
    assertFailsWith<IllegalArgumentException> { validateFixtureFilename("nested/fixture.html") }
    assertFailsWith<IllegalArgumentException> { validateFixtureFilename("..\\outside.html") }
    assertFailsWith<IllegalArgumentException> { validateFixtureFilename("/outside.html") }
  }

  @Test
  fun rejectsBlankFilename() {
    assertFailsWith<IllegalArgumentException> { validateFixtureFilename(" ") }
  }
}
