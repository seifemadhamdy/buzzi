package seifemadhamdy.buzzi.data.repository

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import seifemadhamdy.buzzi.data.mapper.toDomainOrNull
import seifemadhamdy.buzzi.data.remote.NewsApiService
import seifemadhamdy.buzzi.domain.model.Article
import seifemadhamdy.buzzi.domain.repository.ArticleRepository
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

class ArticleRepositoryImpl @Inject constructor(
    private val newsApiService: NewsApiService,
) : ArticleRepository {

    private val articlesByUrl = ConcurrentHashMap<String, Article>()

    override suspend fun getTopHeadlines(): List<Article> =
        coroutineScope {
            val page1 = async {
                newsApiService.getTopHeadlines(page = 1)
            }
            val page2 = async {
                newsApiService.getTopHeadlines(page = 2)
            }

            val articles = (page1.await().articles.orEmpty() +
                    page2.await().articles.orEmpty())
                .mapNotNull { it.toDomainOrNull() }
                .distinctBy { it.url }

            articles.forEach { article ->
                articlesByUrl[article.url] = article
            }

            articles
        }

    override suspend fun searchArticles(query: String): List<Article> {
        val normalizedQuery = query.trim()

        if (normalizedQuery.isBlank()) {
            return emptyList()
        }

        val articles = newsApiService
            .searchTopHeadlines(query = normalizedQuery)
            .articles
            .orEmpty()
            .mapNotNull { it.toDomainOrNull() }
            .distinctBy { it.url }

        articles.forEach { article ->
            articlesByUrl[article.url] = article
        }

        return articles
    }

    override suspend fun getArticleByUrl(url: String): Article? =
        articlesByUrl[url]
}
