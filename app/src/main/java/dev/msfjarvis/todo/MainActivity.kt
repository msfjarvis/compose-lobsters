package dev.msfjarvis.todo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExposurePlus1
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import dev.msfjarvis.todo.data.model.TodoItem
import dev.msfjarvis.todo.ui.TodoRowItem
import dev.msfjarvis.todo.ui.TodoTheme

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      TodoTheme {
        TodoApp()
      }
    }
  }
}

@Composable
fun TodoApp() {
  val items = mutableStateListOf(TodoItem("Default item"))

  Scaffold(
    topBar = { TopAppBar({ Text(text = "I can Compose?") }) },
    floatingActionButton = {
      FloatingActionButton(
        onClick = {
          items.add(TodoItem("Item ${items.size + 1}"))
        },
        elevation = 8.dp,
        modifier = Modifier.semantics { testTag = "fab" }
      ) {
        Icon(asset = Icons.Filled.ExposurePlus1, tint = MaterialTheme.colors.onSurface)
      }
    },
    bodyContent = {
      LazyColumnFor(items = items, modifier = Modifier.padding(horizontal = 16.dp)) { todoItem ->
        TodoRowItem(item = todoItem)
      }
    },
  )
}

@Preview
@Composable
fun PreviewApp() {
  TodoTheme {
    TodoApp()
  }
}
