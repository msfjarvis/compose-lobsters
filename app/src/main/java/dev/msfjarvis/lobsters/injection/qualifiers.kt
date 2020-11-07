package dev.msfjarvis.lobsters.injection

import javax.inject.Qualifier

/**
 * Qualifies a [org.kodein.db.DB] instance that is used to persist saved posts.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SavedPostsDB

/**
 * Qualifies a [org.kodein.db.DB] instance that is used to cache hottest posts.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class HottestPostsDB
