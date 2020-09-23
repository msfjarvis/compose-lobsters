package dev.msfjarvis.lobsters.data.source

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import dev.msfjarvis.lobsters.model.KeybaseSignature
import dev.msfjarvis.lobsters.model.KeybaseSignatureJsonAdapter
import dev.msfjarvis.lobsters.model.LobstersPost
import dev.msfjarvis.lobsters.model.LobstersPostJsonAdapter
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
  fun toKeybaseSignature(value: String?): KeybaseSignature? {
    return value?.let { KeybaseSignatureJsonAdapter(moshi).fromJson(value) }
  }

  @TypeConverter
  @JvmStatic
  fun fromKeybaseSignature(value: KeybaseSignature?): String? {
    return value?.let { KeybaseSignatureJsonAdapter(moshi).toJson(value) }
  }

  @TypeConverter
  @JvmStatic
  fun toLobstersPost(value: String?): LobstersPost? {
    return value?.let { LobstersPostJsonAdapter(moshi).fromJson(value) }
  }

  @TypeConverter
  @JvmStatic
  fun fromLobstersPost(value: LobstersPost?): String? {
    return value?.let { LobstersPostJsonAdapter(moshi).toJson(value) }
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
