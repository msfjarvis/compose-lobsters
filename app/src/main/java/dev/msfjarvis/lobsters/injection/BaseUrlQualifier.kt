package dev.msfjarvis.lobsters.injection

import javax.inject.Qualifier

/**
 * Qualifier for a string value that needs to be provided to the [ApiModule.provideRetrofit] method
 * as the base URL of our API.
 */
@Qualifier @Retention(AnnotationRetention.RUNTIME) annotation class BaseUrlQualifier
