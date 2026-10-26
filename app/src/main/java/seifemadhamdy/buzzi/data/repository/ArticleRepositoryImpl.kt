package seifemadhamdy.buzzi.data.repository

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import seifemadhamdy.buzzi.data.mapper.toDomainOrNull
import seifemadhamdy.buzzi.data.remote.NewsApiService
import seifemadhamdy.buzzi.domain.model.Article
import seifemadhamdy.buzzi.domain.repository.ArticleRepository
import javax.inject.Inject

class ArticleRepositoryImpl @Inject constructor(
    private val newsApiService: NewsApiService,
) : ArticleRepository {

    override suspend fun getTopHeadlines(): List<Article> =
        coroutineScope {
            val page1 = async {
                newsApiService.getTopHeadlines(page = 1)
            }
            val page2 = async {
                newsApiService.getTopHeadlines(page = 2)
            }

            (page1.await().articles.orEmpty() +
                    page2.await().articles.orEmpty())
                .mapNotNull { it.toDomainOrNull() }
                .distinctBy { it.url }
        }

    override suspend fun searchArticles(query: String): List<Article> {
        val normalizedQuery = query.trim()

        if (normalizedQuery.isBlank()) {
            return emptyList()
        }

        return newsApiService
            .searchTopHeadlines(query = normalizedQuery)
            .articles
            .orEmpty()
            .mapNotNull { it.toDomainOrNull() }
            .distinctBy { it.url }
    }
}
