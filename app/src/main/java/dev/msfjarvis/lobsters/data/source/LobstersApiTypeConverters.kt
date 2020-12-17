package dev.msfjarvis.lobsters.data.source

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import dev.msfjarvis.lobsters.model.Submitter
import dev.msfjarvis.lobsters.model.SubmitterJsonAdapter

object LobstersApiTypeConverters {
  private const val SEPARATOR = ","
  private val moshi = Moshi.Builder().build()
  private val submitterAdapter = SubmitterJsonAdapter(moshi)

  @TypeConverter
  @JvmStatic
  fun toSubmitterUser(value: String?): Submitter? {
    return value?.let { submitterAdapter.fromJson(value) }
  }

  @TypeConverter
  @JvmStatic
  fun fromSubmitterUser(value: Submitter?): String? {
    return submitterAdapter.toJson(value)
  }

  @TypeConverter
  @JvmStatic
  fun toTagList(value: String?): List<String>? {
    return value?.split(SEPARATOR)
  }

  @TypeConverter
  @JvmStatic
  fun fromTagList(value: List<String>?): String? {
    return value?.joinToString(SEPARATOR)
  }
}
