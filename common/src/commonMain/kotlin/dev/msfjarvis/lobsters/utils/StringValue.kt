package dev.msfjarvis.lobsters.utils

import androidx.compose.runtime.Composable

@Composable
expect fun stringValue(enum: StringEnum): String

@Composable
expect fun stringValue(enum: StringEnum, arg1: Any): String

@Composable
expect fun stringValue(enum: StringEnum, arg1: Any, arg2: Any): String

@Composable
expect fun stringValue(enum: StringEnum, arg1: Any, arg2: Any, arg3: Any): String