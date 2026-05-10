/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.api.converters

import java.lang.reflect.Type
import kotlin.Unit
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit

object UnitConverter : Converter<ResponseBody, Unit> {
  override fun convert(value: ResponseBody) {
    value.close()
  }

  object Factory : Converter.Factory() {
    override fun responseBodyConverter(
      type: Type,
      annotations: Array<out Annotation>,
      retrofit: Retrofit,
    ): Converter<ResponseBody, *>? {
      return if (type == Unit::class.java) UnitConverter else null
    }
  }
}
