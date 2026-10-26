package seifemadhamdy.buzzi.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import seifemadhamdy.buzzi.data.local.ArticleDatabase
import seifemadhamdy.buzzi.data.local.dao.ArticleDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideArticleDatabase(
        @ApplicationContext context: Context,
    ): ArticleDatabase =
        Room.databaseBuilder(
            context,
            ArticleDatabase::class.java,
            "buzzi.db"
        ).build()

    @Provides
    @Singleton
    fun provideArticleDao(
        database: ArticleDatabase,
    ): ArticleDao =
        database.articleDao()
}
