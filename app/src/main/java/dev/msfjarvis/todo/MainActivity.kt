package dev.msfjarvis.todo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import dev.msfjarvis.todo.compose.utils.IconResource
import dev.msfjarvis.todo.data.model.TodoItem
import dev.msfjarvis.todo.di.Graph
import dev.msfjarvis.todo.ui.TodoRowItem
import dev.msfjarvis.todo.ui.TodoTheme
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      TodoTheme {
        val coroutineScope = rememberCoroutineScope()
        val itemsDao = Graph.database.todoItemsDao()
        val items by itemsDao.getAllItems().collectAsState(initial = emptyList())
        TodoApp(
          items,
          { item -> coroutineScope.launch { itemsDao.insert(item) } },
          { item -> coroutineScope.launch { itemsDao.delete(item) } },
        )
      }
    }
  }
}

@Composable
fun TodoApp(
  items: List<TodoItem>,
  onAdd: (item: TodoItem) -> Unit,
  onDelete: (item: TodoItem) -> Unit,
) {

  Scaffold(
    topBar = { TopAppBar({ Text(text = "I can Compose?") }) },
    floatingActionButton = {
      FloatingActionButton(
        onClick = { onAdd.invoke(TodoItem("Item ${items.size + 1}")) },
        elevation = 8.dp,
        modifier = Modifier.semantics { testTag = "fab" }
      ) {
        IconResource(
          resourceId = R.drawable.ic_exposure_plus_1_24dp,
          tint = MaterialTheme.colors.onSurface,
        )
      }
    },
    bodyContent = {
      LazyColumnFor(
        items = items,
        modifier = Modifier.padding(horizontal = 16.dp)
      ) { todoItem ->
        TodoRowItem(item = todoItem) { onDelete.invoke(todoItem) }
      }
    },
  )
}

@Preview
@Composable
fun PreviewApp() {
  TodoTheme {
    val items = arrayListOf(TodoItem("Item 1"))
    TodoApp(
      items,
      items::add,
      items::remove,
    )
  }
}
