package dev.msfjarvis.lobsters.data.source

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import dev.msfjarvis.lobsters.model.Submitter
import dev.msfjarvis.lobsters.model.SubmitterJsonAdapter

object LobstersApiTypeConverters {
  private val moshi = Moshi.Builder().build()
  private const val SEPARATOR = ","

  @TypeConverter
  @JvmStatic
  fun toSubmitterUser(value: String?): Submitter? {
    return value?.let { SubmitterJsonAdapter(moshi).fromJson(value) }
  }

  @TypeConverter
  @JvmStatic
  fun fromSubmitterUser(value: Submitter?): String? {
    return value?.let { SubmitterJsonAdapter(moshi).toJson(value) }
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
