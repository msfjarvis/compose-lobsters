/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.smoketests

import dev.msfjarvis.claw.api.LobstersParserClient
import dev.msfjarvis.claw.parser.LobstersParserService
import dev.msfjarvis.claw.parser.LobstersParserServiceImpl
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding

@Inject
@ContributesBinding(AppScope::class, binding = binding<LobstersParserClient>())
class JvmParserClient : LobstersParserClient {
  private val service = LobstersParserServiceImpl()

  override suspend fun service(): LobstersParserService = service
}
