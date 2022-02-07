package dev.msfjarvis.claw.common.res

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource

actual val commentIcon
  @Composable get() = painterResource("comment_black_24dp.svg")
actual val heartIcon
  @Composable get() = painterResource("favorite_black_24dp.svg")
actual val heartBorderIcon
  @Composable get() = painterResource("favorite_border_black_24dp.svg")
