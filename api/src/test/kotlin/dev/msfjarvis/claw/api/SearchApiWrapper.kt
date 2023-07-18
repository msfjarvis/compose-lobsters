/*
 * Copyright © 2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.api

import com.slack.eithernet.ApiResult.Companion.success
import com.slack.eithernet.test.EitherNetController
import com.slack.eithernet.test.enqueue
import dev.msfjarvis.claw.api.converters.SearchConverter
import dev.msfjarvis.claw.util.TestUtils.getResource
import okhttp3.MediaType
import okhttp3.ResponseBody

class SearchApiWrapper(controller: EitherNetController<LobstersSearchApi>) {
  val api = controller.api

  init {
    controller.enqueue(LobstersSearchApi::searchPosts) {
      success(
        SearchConverter.convert(
          ResponseBody.create(MediaType.get("text/html"), getResource("search_chatgpt_page.html"))
        )
      )
    }
  }
}
