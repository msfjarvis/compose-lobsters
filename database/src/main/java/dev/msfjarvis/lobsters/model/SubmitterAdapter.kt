package dev.msfjarvis.lobsters.model

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.sqldelight.ColumnAdapter

class SubmitterAdapter : ColumnAdapter<Submitter, String> {
  private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
  private val submitterJsonAdapter = moshi.adapter(Submitter::class.java)

  override fun decode(databaseValue: String): Submitter {
    return submitterJsonAdapter.fromJson(databaseValue)!!
  }

  override fun encode(value: Submitter): String {
    return submitterJsonAdapter.toJson(value)
  }
}
