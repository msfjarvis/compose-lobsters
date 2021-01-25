package dev.msfjarvis.lobsters.model

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import com.squareup.sqldelight.ColumnAdapter
import dev.zacsweers.moshix.reflect.MetadataKotlinJsonAdapterFactory

@OptIn(ExperimentalStdlibApi::class)
class SubmitterAdapter : ColumnAdapter<Submitter, String> {
  private val moshi = Moshi.Builder().add(MetadataKotlinJsonAdapterFactory()).build()
  private val submitterJsonAdapter = moshi.adapter<Submitter>()

  override fun decode(databaseValue: String): Submitter {
    return submitterJsonAdapter.fromJson(databaseValue)!!
  }

  override fun encode(value: Submitter): String {
    return submitterJsonAdapter.toJson(value)
  }
}
