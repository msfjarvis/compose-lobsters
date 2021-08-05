package dev.msfjarvis.claw.common.res

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import dev.msfjarvis.claw.common.R

actual val commentIcon
  @Composable get() = painterResource(R.drawable.ic_insert_comment_24dp)
actual val heartIcon
  @Composable get() = painterResource(R.drawable.ic_favorite_24dp)
actual val heartBorderIcon
  @Composable get() = painterResource(R.drawable.ic_favorite_border_24dp)
