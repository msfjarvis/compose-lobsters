package dev.msfjarvis.lobsters.model

import com.squareup.moshi.JsonAdapter
import com.squareup.sqldelight.ColumnAdapter

class SubmitterAdapter(private val submitterJsonAdapter: JsonAdapter<Submitter>) :
  ColumnAdapter<Submitter, String> {

  override fun decode(databaseValue: String): Submitter {
    return submitterJsonAdapter.fromJson(databaseValue)!!
  }

  override fun encode(value: Submitter): String {
    return submitterJsonAdapter.toJson(value)
  }
}
