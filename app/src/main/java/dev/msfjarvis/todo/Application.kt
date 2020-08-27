package dev.msfjarvis.todo

import android.app.Application
import dev.msfjarvis.todo.di.Graph

class Application : Application() {
  override fun onCreate() {
    super.onCreate()
    Graph.provide(this)
  }
}
