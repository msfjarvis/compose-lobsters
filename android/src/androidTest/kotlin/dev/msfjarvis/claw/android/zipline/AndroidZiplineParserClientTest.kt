/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.zipline

import androidx.test.platform.app.InstrumentationRegistry
import dev.msfjarvis.claw.android.ClawApplication
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

@OptIn(DelicateCoroutinesApi::class)
class AndroidZiplineParserClientTest {
  @Test
  fun ziplineLoadingSucceeds() {
    val context =
      InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
        as ClawApplication

    val parserClient = context.appGraph.ziplineClient

    GlobalScope.launch {
      try {
        parserClient
          .service()
          .parseReplyForm(
            """
            <div class="comment_form_container" data-shortid="">
            <form action="/comments" accept-charset="UTF-8" method="post"><input type="hidden" name="authenticity_token" value="AI0414bnzi152-mE0JTWEtwq5B0ZhALBW1W7rGiG5zR-sFaJjWzdARXFM7w_DbPQqWNjzh9bufWZbXG39v5T6g" autocomplete="off">

              <input value="znlkib" autocomplete="off" type="hidden" name="story_id" id="story_id" />
              <input value="post" autocomplete="off" type="hidden" name="_method" id="_method" />


                <input value="edtrox" autocomplete="off" type="hidden" name="parent_comment_short_id" id="parent_comment_short_id" />


              <div style="width: 100%;">
                <textarea rows="5" placeholder="" required="required" name="comment" id="comment">
            </textarea>

                <div class="controls">
                  <div class="buttons">
                    <input type="submit" name="commit" value="Post" class="comment-post" data-disable-with="Post">
                  </div>
                </div>
              </div>
            </form>
            </div>
            """
              .trimIndent()
          )
      } catch (e: Exception) {
        fail(e)
      }
    }
  }
}
