package dev.msfjarvis.lobsters.utils

import androidx.compose.runtime.Composable

@Composable
expect fun stringValue(enum: StringEnum, vararg formatArgs: Any): String