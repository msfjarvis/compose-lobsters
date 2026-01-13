/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.ui

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class HTMLTest {
  @Test
  fun `preprocessHtml removes paragraph tags from list items`() {
    val input = TEST_HTML
    val result = preprocessHtml(input)

    assertThat(result).doesNotContain("<li>\n        <p>I set up hosting")
    assertThat(result).doesNotContain("<li>\n        <p>Beyond uploading")
    assertThat(result).contains("<li>")
    assertThat(result).contains("I set up hosting")
    assertThat(result).contains("Beyond uploading files")
  }

  @Test
  fun `preprocessHtml preserves HTML formatting inside removed paragraphs`() {
    val input = TEST_HTML
    val result = preprocessHtml(input)

    assertThat(result)
      .contains("<a href=\"https://www.tigertech.net\" rel=\"ugc\">tigertech.net</a>")
    assertThat(result).contains("<code>.htaccess</code>")
    assertThat(result).contains("<code>browsersync</code>")
    assertThat(result).contains("<em>somewhere</em>")
    assertThat(result).contains("<strong>away from them</strong>")
  }

  @Test
  fun `preprocessHtml preserves nested ordered lists`() {
    val input = TEST_HTML
    val result = preprocessHtml(input)

    assertThat(result).contains("<ol>")
    assertThat(result).contains("</ol>")
    assertThat(result).contains("I don't trust GitHub long term")
    assertThat(result).contains("I don't want to become product support")
    assertThat(result).contains("Owning your own domain")
  }

  @Test
  fun `preprocessHtml preserves paragraphs outside list items`() {
    val input = TEST_HTML
    val result = preprocessHtml(input)

    assertThat(result).contains("<p>Most lobsters I encounter")
    assertThat(result).contains("<p>I only set up my own website")
    assertThat(result).contains("<p>To be clear, I am not affiliated")
  }

  @Test
  fun `preprocessHtml inserts br tags between list items`() {
    val input = TEST_HTML
    val result = preprocessHtml(input)

    assertThat(result).containsMatch("</li>\\s*<br>\\s*<li>")
  }

  companion object {
    private val TEST_HTML =
      """
      |<p>Most lobsters I encounter already have websites, but for those of you reading this who don't, go change that! I believe in you!</p>
      |<hr>
      |<blockquote>
      |<p>If you don't have a domain or hosting yet, now's the time to buckle down and do that. Unfortunately, I don't have good advice for you here. Just know that it's going to be stupid and tedious and bad and unfun. That's just the way this is.</p>
      |</blockquote>
      |<p>I only set up my own website around a year ago, but I've actually had quite a different experience to the author. Here's my advice:<br>
      |(assuming you're new to this 🙂)</p>
      |<ul>
      |<li>
      |<p>I set up hosting with <a href="https://www.tigertech.net" rel="ugc">tigertech.net</a>, and it has been a joy. They host <a href="https://www.tigertech.net/customerlist/700" rel="ugc">cppreference.com</a>, so they can handle your blog, and their help articles are all human written and straight to the point. Their prices are fair, and every request I've had has been responded to within a day by a real person.</p>
      |</li>
      |<li>
      |<p>Beyond uploading files to their server, the only thing I've changed is adding an Apache <a href="https://support.tigertech.net/htaccess-topic" rel="ugc"><code>.htaccess</code> file</a>. This is something I had no clue about before this, but Apache is a standard choice for a server, and the <code>.htaccess</code> file gives you more control than you need. It's organized around modules, like <code>mod rewrite</code> or <code>mod deflate</code> that are all massively documented online. For example, I use <code>mod rewrite</code> to upload <code>/xyz.html</code> files for my pages but redirect to <code>/xyz</code> for cleaner links.</p>
      |</li>
      |<li>
      |<p>To preview your site locally as it would be served remotely, you should download Apache and set it up to serve on localhost. I also pair this with the <code>browsersync</code> utility that proxies the Apache server and creates a "websocket" that lets it tell the browser to refresh whenever a file changes. This is the only reason I've installed <code>nodejs</code> and I intend to keep it that way.</p>
      |</li>
      |<li>
      |<p>Why go with a hosting provider and not self-hosting or github.io?</p>
      |<ol>
      |<li>I don't trust GitHub long term and don't want to depend on them for such a core part of my online presence.</li>
      |<li>I don't want to become product support for my own self-hosted solution. I'm more than happy to pay someone for that, and tigertech includes automatic site backups because they actually care about you.</li>
      |<li>Owning your own domain is ~~cool-as-fuck~~, but it needs to be purchased and registered <em>somewhere</em>. Tigertech is also a standard registrar, is as-cheap as any other registrar if you aren't doing hosting, and actually has a help page for <a href="https://support.tigertech.net/domain-transfer-away" rel="ugc">transferring your domain <strong>away from them</strong></a>.</li>
      |</ol>
      |</li>
      |</ul>
      |<p>To be clear, I am not affiliated with tigertech, but they've been great for me. They feel like an amazing old-internet company that's just doing everything right and I think people should be aware of them.</p>
      |"""
        .trimMargin()
  }
}
