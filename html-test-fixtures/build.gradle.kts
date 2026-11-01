/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
plugins {
  id("dev.msfjarvis.claw.kotlin-jvm")
  id("dev.msfjarvis.claw.update-fixtures")
  `java-test-fixtures`
}

fixtures {
  fixtures.put("https://lobste.rs/", "csrf_page.html")
  fixtures.put("https://lobste.rs/", "hottest_page.html")
  fixtures.put("https://lobste.rs/~msfjarvis", "msfjarvis.html")
  fixtures.put("https://lobste.rs/s/d7m5j1", "post_details_d7m5j1.html")
  fixtures.put("https://lobste.rs/s/ktew3s", "post_details_ktew3s.html")
  fixtures.put("https://lobste.rs/s/tdfoqh", "post_details_tdfoqh.html")
  // This is behind Anubis
  // fixtures.put("https://lobste.rs/search?q=chatgpt&what=comments&order=newest",
  // "search_chatgpt_page.html")
  fixtures.put("https://lobste.rs/filters", "tags.html")
}
