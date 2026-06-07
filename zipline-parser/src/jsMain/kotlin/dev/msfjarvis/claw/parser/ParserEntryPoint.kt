/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.parser

import app.cash.zipline.Zipline
import dev.msfjarvis.claw.parser.model.ParserSerializersModule
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@OptIn(ExperimentalJsExport::class)
@JsExport
fun launchZipline() {
  Zipline.get(ParserSerializersModule)
    .bind<LobstersParserService>(
      name = "LobstersParserService",
      instance = LobstersParserServiceImpl(),
    )
}
