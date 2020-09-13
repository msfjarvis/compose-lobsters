package dev.msfjarvis.todo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import dagger.hilt.android.AndroidEntryPoint
import dev.msfjarvis.todo.compose.utils.IconResource
import dev.msfjarvis.todo.data.model.TodoItem
import dev.msfjarvis.todo.data.source.TodoDatabase
import dev.msfjarvis.todo.ui.TodoRowItem
import dev.msfjarvis.todo.ui.TodoTheme
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
  @Inject lateinit var database: TodoDatabase

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      TodoTheme {
        val coroutineScope = rememberCoroutineScope()
        val itemsDao = database.todoItemsDao()
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
      modifier = Modifier.testTag("item_dialog")
    )
  }

  Scaffold(
    topBar = { TopAppBar({ Text(text = stringResource(R.string.app_name)) }) },
    floatingActionButton = {
      FloatingActionButton(
        onClick = { showingDialog.value = true },
        elevation = 8.dp,
        modifier = Modifier.testTag("fab")
      ) {
        IconResource(
          resourceId = R.drawable.ic_exposure_plus_1_24dp,
          tint = MaterialTheme.colors.onSecondary,
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
  modifier: Modifier = Modifier,
) {
  var newItemName by mutableStateOf(TextFieldValue(""))
  val hideDialog = { showingDialog.value = false }
  AlertDialog(
    onDismissRequest = hideDialog,
    text = {
      OutlinedTextField(
        activeColor = MaterialTheme.colors.secondary,
        value = newItemName,
        onValueChange = { newItemName = it },
        label = { Text(text = "Name") },
        modifier = Modifier.testTag("item_name")
      )
    },
    confirmButton = {
      Button(
        onClick = {
          if (newItemName.text.isNotEmpty()) {
            onAdd.invoke(TodoItem(newItemName.text))
            newItemName = TextFieldValue("")
            hideDialog.invoke()
          }
        },
        modifier = Modifier.testTag("add_button")
      ) {
        Text(text = "Add")
      }
    },
    modifier = Modifier then modifier,
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
