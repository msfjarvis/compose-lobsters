package dev.msfjarvis.lobsters.injection

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.msfjarvis.lobsters.model.LobstersPost
import org.kodein.db.DB
import org.kodein.db.impl.open
import org.kodein.db.orm.kotlinx.KotlinxSerializer

@Module
@InstallIn(ActivityComponent::class)
object SavedPostsDBModule {

  @Provides
  @SavedPostsDB
  fun provideSavedPostsDB(@ApplicationContext context: Context): DB {
    return DB.open("${context.filesDir.resolve("saved_posts.db")}", KotlinxSerializer {
      +LobstersPost.serializer()
    })
  }
}
