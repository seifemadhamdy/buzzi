package seifemadhamdy.buzzi.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import seifemadhamdy.buzzi.data.repository.ArticleRepositoryImpl
import seifemadhamdy.buzzi.domain.repository.ArticleRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

  @Binds
  @Singleton
  abstract fun bindArticleRepository(implementation: ArticleRepositoryImpl): ArticleRepository
}
