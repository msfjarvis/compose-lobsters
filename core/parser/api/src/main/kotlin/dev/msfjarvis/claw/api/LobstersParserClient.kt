/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.api

import dev.msfjarvis.claw.parser.LobstersParserService

interface LobstersParserClient {
  suspend fun service(): LobstersParserService
}
