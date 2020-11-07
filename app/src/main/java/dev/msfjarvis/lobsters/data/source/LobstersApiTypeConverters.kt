package dev.msfjarvis.lobsters.data.source

import androidx.room.TypeConverter
import dev.msfjarvis.lobsters.model.Submitter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object LobstersApiTypeConverters {
  private const val SEPARATOR = ","

  @TypeConverter
  @JvmStatic
  fun toSubmitterUser(value: String?): Submitter? {
    return value?.let { Json.decodeFromString(value) }
  }

  @TypeConverter
  @JvmStatic
  fun fromSubmitterUser(value: Submitter?): String? {
    return value?.let { Json.encodeToString(value) }
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
