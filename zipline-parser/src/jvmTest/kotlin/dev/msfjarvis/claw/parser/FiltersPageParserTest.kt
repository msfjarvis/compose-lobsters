/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FiltersPageParserTest {
  private val service = LobstersParserServiceImpl()

  @Test
  fun parsesCheckedTagsAndMetadata() {
    val page = service.parseFiltersPage(FILTERS_PAGE_HTML)

    assertEquals("filters-token", page.authenticityToken)
    assertEquals(setOf("kotlin"), page.blockedTags)
    assertEquals(2, page.tags.size)

    val kotlinTag = page.tags.first { it.tag == "kotlin" }
    assertEquals("Kotlin discussions", kotlinTag.description)
    assertEquals("language", kotlinTag.category)
    assertTrue(kotlinTag.active)
    assertFalse(kotlinTag.privileged)
    assertFalse(kotlinTag.isMedia)
    assertEquals(1.2, kotlinTag.hotnessMod)

    val inactiveTag = page.tags.first { it.tag == "inactive" }
    assertEquals("Inactive tag description", inactiveTag.description)
    assertEquals("topic", inactiveTag.category)
    assertFalse(inactiveTag.active)
    assertTrue(inactiveTag.privileged)
    assertTrue(inactiveTag.isMedia)
    assertEquals(0.3, inactiveTag.hotnessMod)
  }
}

private const val FILTERS_PAGE_HTML =
  """
  <!doctype html>
  <html>
    <body>
      <form action="/filters" method="post">
        <input type="hidden" name="authenticity_token" value="filters-token" />
        <table>
          <tr data-category="language" data-hotness-mod="1.2">
            <td>
              <label>
                <input type="checkbox" name="tags[kotlin]" checked="checked" />
                <a class="tag" href="/t/kotlin">kotlin</a>
                <span>Kotlin discussions</span>
              </label>
            </td>
          </tr>
          <tr data-category="topic" data-privileged="true" data-hotness-mod="0.3">
            <td>
              <label>
                <input type="checkbox" name="tags[inactive]" />
                <a class="tag tag_is_media" href="/t/inactive">inactive</a>
                <span class="inactive_tag">Inactive tag description</span>
              </label>
            </td>
          </tr>
        </table>
      </form>
    </body>
  </html>
  """
