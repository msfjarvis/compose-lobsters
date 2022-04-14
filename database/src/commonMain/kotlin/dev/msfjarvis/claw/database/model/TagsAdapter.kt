package dev.msfjarvis.claw.database.model

import app.cash.sqldelight.ColumnAdapter

class TagsAdapter : ColumnAdapter<List<String>, String> {
  override fun decode(databaseValue: String): List<String> {
    return databaseValue.split(SEPARATOR)
  }

  override fun encode(value: List<String>): String {
    return value.joinToString(SEPARATOR)
  }

  private companion object {
    private const val SEPARATOR = ","
  }
}
