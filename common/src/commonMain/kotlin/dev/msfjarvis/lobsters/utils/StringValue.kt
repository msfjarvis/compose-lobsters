package dev.msfjarvis.lobsters.utils

import androidx.compose.runtime.Composable

@Composable
expect fun Strings.get(): String

@Composable
expect fun Strings.get(fmt: Any): String
