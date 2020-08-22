package dev.msfjarvis.todo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExposurePlus1
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.ui.tooling.preview.Preview
import dev.msfjarvis.todo.data.TodoItem
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

@Preview
@Composable
fun TodoApp() {
  val items = remember { mutableStateListOf(TodoItem("Default item")) }

  Scaffold(
    topBar = { TopAppBar({ Text(text = "I can Compose?") }) },
    floatingActionButton = {
      FloatingActionButton(
        onClick = {
          items.add(TodoItem("Item ${items.size + 1}"))
        },
        elevation = 8.dp,
      ) {
        Icon(asset = Icons.Filled.ExposurePlus1, tint = MaterialTheme.colors.onSurface)
      }
    },
    bodyContent = {
      LazyColumnFor(items = items, modifier = Modifier.padding(horizontal = 16.dp)) { todoItem ->
        TodoRowItem(item = todoItem)
      }
    }
  )
}

@Composable
fun LazyItemScope.TodoRowItem(item: TodoItem) {
  Row(
    modifier = Modifier.padding(vertical = 8.dp).fillParentMaxWidth(),
  ) {
    Card(
      shape = RoundedCornerShape(8.dp),
      modifier = Modifier.fillParentMaxWidth(),
    ) {
      Text(
        text = item.title,
        style = TextStyle(
          fontSize = 20.sp,
          textAlign = TextAlign.Center
        ),
        modifier = Modifier.padding(16.dp),
      )
    }
  }
}
