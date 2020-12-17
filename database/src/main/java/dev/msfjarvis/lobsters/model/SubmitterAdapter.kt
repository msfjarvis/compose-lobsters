package dev.msfjarvis.lobsters.model

import com.squareup.moshi.Moshi
import com.squareup.sqldelight.ColumnAdapter

class SubmitterAdapter : ColumnAdapter<Submitter, String> {
  private val moshi = Moshi.Builder().build()
  private val submitterJsonAdapter = SubmitterJsonAdapter(moshi)

  override fun decode(databaseValue: String): Submitter {
    return submitterJsonAdapter.fromJson(databaseValue)!!
  }

  override fun encode(value: Submitter): String {
    return submitterJsonAdapter.toJson(value)
  }
}
