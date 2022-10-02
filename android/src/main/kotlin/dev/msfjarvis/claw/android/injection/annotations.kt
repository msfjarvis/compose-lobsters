package dev.msfjarvis.claw.android.injection

import javax.inject.Qualifier

@Qualifier @Retention(AnnotationRetention.RUNTIME) annotation class DatabaseDispatcher

@Qualifier @Retention(AnnotationRetention.RUNTIME) annotation class MainDispatcher

@Qualifier @Retention(AnnotationRetention.RUNTIME) annotation class IODispatcher
