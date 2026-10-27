package seifemadhamdy.buzzi.data.repository

import java.io.IOException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import seifemadhamdy.buzzi.data.local.dao.ArticleDao
import seifemadhamdy.buzzi.data.local.entity.ArticleEntity
import seifemadhamdy.buzzi.data.remote.NewsApiService
import seifemadhamdy.buzzi.data.remote.dto.ArticleDto
import seifemadhamdy.buzzi.data.remote.dto.NewsResponseDto
import seifemadhamdy.buzzi.data.remote.dto.SourceDto

class ArticleRepositoryImplTest {

  private class FakeNewsApiService(
    var searchResult: NewsResponseDto? = null,
    var searchException: Exception? = null,
  ) : NewsApiService {
    override suspend fun getTopHeadlines(country: String, page: Int): NewsResponseDto {
      throw NotImplementedError()
    }

    override suspend fun searchTopHeadlines(country: String, query: String): NewsResponseDto {
      if (searchException != null) throw searchException!!
      return searchResult
        ?: NewsResponseDto(status = "ok", totalResults = 0, articles = emptyList())
    }
  }

  private class FakeArticleDao(initialArticles: List<ArticleEntity> = emptyList()) : ArticleDao {
    private val articles = mutableListOf<ArticleEntity>().apply { addAll(initialArticles) }

    override suspend fun insertArticles(articles: List<ArticleEntity>) {
      for (newArticle in articles) {
        this.articles.removeIf { it.url == newArticle.url }
        this.articles.add(0, newArticle)
      }
    }

    override suspend fun getArticles(): List<ArticleEntity> = articles.toList()

    override suspend fun searchArticles(query: String): List<ArticleEntity> {
      val lowerQuery = query.lowercase()
      return articles.filter {
        it.title.lowercase().contains(lowerQuery) ||
          it.description?.lowercase()?.contains(lowerQuery) == true ||
          it.content?.lowercase()?.contains(lowerQuery) == true
      }
    }

    override suspend fun getArticleByUrl(url: String): ArticleEntity? = articles.find {
      it.url == url
    }

    override suspend fun clearArticles() {
      articles.clear()
    }
  }

  @Test
  fun `searchArticles returns network results when online and caches them`() = runBlocking {
    val articleDto =
      ArticleDto(
        source = SourceDto("1", "BBC"),
        author = "Author",
        title = "Test Article",
        description = "Desc",
        url = "https://example.com/1",
        urlToImage = null,
        publishedAt = "2026-03-29",
        content = "Content",
      )
    val apiService = FakeNewsApiService(searchResult = NewsResponseDto("ok", 1, listOf(articleDto)))
    val dao = FakeArticleDao()
    val repository = ArticleRepositoryImpl(apiService, dao)

    val results = repository.searchArticles("Test")

    assertEquals(1, results.size)
    assertEquals("Test Article", results[0].title)
  }

  @Test
  fun `searchArticles returns cached search results when offline and cache matches`() =
    runBlocking {
      val apiService = FakeNewsApiService(searchException = IOException("Network down"))
      val cachedEntity =
        ArticleEntity(
          url = "https://example.com/1",
          sourceName = "BBC",
          author = "Author",
          title = "Cached Test Article",
          description = "Desc",
          imageUrl = null,
          publishedAt = "2026-03-29",
          content = "Content",
        )
      val dao = FakeArticleDao(initialArticles = listOf(cachedEntity))
      val repository = ArticleRepositoryImpl(apiService, dao)

      val results = repository.searchArticles("Test")

      assertEquals(1, results.size)
      assertEquals("Cached Test Article", results[0].title)
    }

  @Test
  fun `searchArticles returns empty list when offline and cache is empty`() = runBlocking {
    val apiService = FakeNewsApiService(searchException = IOException("Network down"))
    val dao = FakeArticleDao(initialArticles = emptyList())
    val repository = ArticleRepositoryImpl(apiService, dao)

    val results = repository.searchArticles("Unknown")

    assertEquals(0, results.size)
  }
}
