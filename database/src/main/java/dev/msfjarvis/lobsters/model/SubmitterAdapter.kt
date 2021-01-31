package dev.msfjarvis.lobsters.model

import com.squareup.moshi.JsonAdapter
import com.squareup.sqldelight.ColumnAdapter
import javax.inject.Inject

class SubmitterAdapter @Inject constructor(private val submitterJsonAdapter: JsonAdapter<Submitter>) :
  ColumnAdapter<Submitter, String> {

  override fun decode(databaseValue: String): Submitter {
    return submitterJsonAdapter.fromJson(databaseValue)!!
  }

  override fun encode(value: Submitter): String {
    return submitterJsonAdapter.toJson(value)
  }
}
