package dev.msfjarvis.lobsters.api

import java.io.File

object TestUtils {
  fun getJson(path : String) : String {
    // Load the JSON response
    val uri = javaClass.classLoader.getResource(path)
    val file = File(uri.path)
    return String(file.readBytes())
  }
}
