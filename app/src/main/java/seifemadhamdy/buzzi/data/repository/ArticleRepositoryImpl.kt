package seifemadhamdy.buzzi.data.repository

import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import seifemadhamdy.buzzi.data.local.dao.ArticleDao
import seifemadhamdy.buzzi.data.mapper.toDomain
import seifemadhamdy.buzzi.data.mapper.toEntityOrNull
import seifemadhamdy.buzzi.data.remote.NewsApiService
import seifemadhamdy.buzzi.domain.model.Article
import seifemadhamdy.buzzi.domain.repository.ArticleRepository

class ArticleRepositoryImpl
@Inject
constructor(
  private val newsApiService: NewsApiService,
  private val articleDao: ArticleDao,
) : ArticleRepository {

  override suspend fun getTopHeadlines(): List<Article> =
    try {
      coroutineScope {
        val page1 = async {
          newsApiService.getTopHeadlines(page = 1)
        }
        val page2 = async {
          newsApiService.getTopHeadlines(page = 2)
        }

        val entities =
          (page1.await().articles.orEmpty() + page2.await().articles.orEmpty())
            .mapNotNull { it.toEntityOrNull() }
            .distinctBy { it.url }

        if (entities.isNotEmpty()) {
          articleDao.insertArticles(entities)
        }

        entities.map { it.toDomain() }
      }
    } catch (exception: Exception) {
      if (exception is CancellationException) throw exception
      val cachedArticles = articleDao.getArticles().map { it.toDomain() }
      cachedArticles.ifEmpty {
        throw exception
      }
    }

  override suspend fun searchArticles(query: String): List<Article> {
    val normalizedQuery = query.trim()

    if (normalizedQuery.isBlank()) {
      return emptyList()
    }

    try {
      val entities =
        newsApiService
          .searchTopHeadlines(query = normalizedQuery)
          .articles
          .orEmpty()
          .mapNotNull { it.toEntityOrNull() }
          .distinctBy { it.url }

      if (entities.isNotEmpty()) {
        articleDao.insertArticles(entities)
      }
    } catch (exception: Exception) {
      if (exception is CancellationException) throw exception
    }

    return articleDao.searchArticles(normalizedQuery).map { it.toDomain() }
  }

  override suspend fun getArticleByUrl(url: String): Article? =
    articleDao.getArticleByUrl(url)?.toDomain()
}
