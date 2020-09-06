package dev.msfjarvis.todo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.AlertDialog
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
  val showingDialog = remember { mutableStateOf(false) }

  if (showingDialog.value) {
    ItemAddDialog(
      showingDialog = showingDialog,
      onAdd = onAdd,
    )
  }

  Scaffold(
    topBar = { TopAppBar({ Text(text = "I can Compose?") }) },
    floatingActionButton = {
      FloatingActionButton(
        onClick = { showingDialog.value = true },
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

@Composable
fun ItemAddDialog(
  showingDialog: MutableState<Boolean>,
  onAdd: (item: TodoItem) -> Unit,
) {
  var newItemName by mutableStateOf(TextFieldValue(""))
  AlertDialog(
    onDismissRequest = {
      showingDialog.value = false
    },
    text = {
      OutlinedTextField(
        value = newItemName,
        onValueChange = { newItemName = it },
        label = {
          Text(
            text = "Name",
          )
        },
      )
    },
    title = {
      Text(
        text = "Create new item",
      )
    },
    confirmButton = {
      Text(
        text = "Add",
        modifier = Modifier.padding(16.dp).clickable(onClick = {
          onAdd.invoke(TodoItem(newItemName.text))
          newItemName = TextFieldValue("")
          showingDialog.value = false
        }),
        style = TextStyle(
          fontSize = 18.sp,
          textAlign = TextAlign.End,
          fontStyle = FontStyle.Normal,
        ),
      )
    }
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
