package dev.msfjarvis.todo.di

import android.content.Context
import androidx.room.Room
import dev.msfjarvis.todo.data.source.TodoDatabase

/**
 * Rudimentary DI container to initialize singletons, will be switched to Hilt when architecture becomes
 * a focus again.
 */
object Graph {

  lateinit var database: TodoDatabase

  fun provide(context: Context) {
    database = Room.databaseBuilder(context, TodoDatabase::class.java, "data.db")
      .fallbackToDestructiveMigration().build()
  }
}
