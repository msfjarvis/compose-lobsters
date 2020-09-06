package dev.msfjarvis.todo.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun TodoTheme(children: @Composable () -> Unit) {
  MaterialTheme(
    content = children,
  )
}
