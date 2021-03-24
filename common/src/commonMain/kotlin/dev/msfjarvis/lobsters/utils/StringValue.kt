package dev.msfjarvis.lobsters.utils

import androidx.compose.runtime.Composable

@Composable
expect fun stringValue(enum: Strings): String

/**
 * Workaround for https://youtrack.jetbrains.com/issue/KT-44499
 *
 */
@Composable
expect fun stringValue(enum: Strings, arg1: Any): String

@Composable
expect fun stringValue(enum: Strings, arg1: Any, arg2: Any): String

@Composable
expect fun stringValue(enum: Strings, arg1: Any, arg2: Any, arg3: Any): String
