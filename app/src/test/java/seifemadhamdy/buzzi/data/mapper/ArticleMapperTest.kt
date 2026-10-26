package seifemadhamdy.buzzi.data.mapper

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import seifemadhamdy.buzzi.data.local.entity.ArticleEntity
import seifemadhamdy.buzzi.data.remote.dto.ArticleDto
import seifemadhamdy.buzzi.data.remote.dto.SourceDto
import seifemadhamdy.buzzi.domain.model.Article

class ArticleMapperTest {

  @Test
  fun `toEntityOrNull converts valid ArticleDto to ArticleEntity`() {
    val dto =
      ArticleDto(
        source = SourceDto(id = "1", name = "BBC"),
        author = "Author",
        title = "  Test Title  ",
        description = "Description",
        url = "https://example.com/1",
        urlToImage = "https://example.com/image.jpg",
        publishedAt = "2026-03-29",
        content = "Content",
      )

    val entity = dto.toEntityOrNull()

    assertEquals("BBC", entity?.sourceName)
    assertEquals("Author", entity?.author)
    assertEquals("Test Title", entity?.title)
    assertEquals("Description", entity?.description)
    assertEquals("https://example.com/1", entity?.url)
    assertEquals("https://example.com/image.jpg", entity?.imageUrl)
    assertEquals("2026-03-29", entity?.publishedAt)
    assertEquals("Content", entity?.content)
  }

  @Test
  fun `toEntityOrNull returns null when title or url is blank or null`() {
    val dtoWithNoTitle =
      ArticleDto(
        source = null,
        author = null,
        title = "   ",
        description = "Desc",
        url = "https://example.com",
        urlToImage = null,
        publishedAt = null,
        content = null,
      )

    val dtoWithNoUrl =
      ArticleDto(
        source = null,
        author = null,
        title = "Title",
        description = "Desc",
        url = null,
        urlToImage = null,
        publishedAt = null,
        content = null,
      )

    assertNull(dtoWithNoTitle.toEntityOrNull())
    assertNull(dtoWithNoUrl.toEntityOrNull())
  }

  @Test
  fun `toDomain converts ArticleEntity to Article`() {
    val entity =
      ArticleEntity(
        url = "https://example.com/1",
        sourceName = "TechCrunch",
        author = "Writer",
        title = "News Title",
        description = "News Desc",
        imageUrl = "https://example.com/img.png",
        publishedAt = "2026-03-29T12:00:00Z",
        content = "Full content",
      )

    val domain = entity.toDomain()

    assertEquals("TechCrunch", domain.sourceName)
    assertEquals("Writer", domain.author)
    assertEquals("News Title", domain.title)
    assertEquals("News Desc", domain.description)
    assertEquals("https://example.com/1", domain.url)
    assertEquals("https://example.com/img.png", domain.imageUrl)
    assertEquals("2026-03-29T12:00:00Z", domain.publishedAt)
    assertEquals("Full content", domain.content)
  }

  @Test
  fun `toEntity converts Article to ArticleEntity`() {
    val article =
      Article(
        sourceName = "TechCrunch",
        author = "Writer",
        title = "News Title",
        description = "News Desc",
        url = "https://example.com/1",
        imageUrl = "https://example.com/img.png",
        publishedAt = "2026-03-29T12:00:00Z",
        content = "Full content",
      )

    val entity = article.toEntity()

    assertEquals("TechCrunch", entity.sourceName)
    assertEquals("Writer", entity.author)
    assertEquals("News Title", entity.title)
    assertEquals("News Desc", entity.description)
    assertEquals("https://example.com/1", entity.url)
    assertEquals("https://example.com/img.png", entity.imageUrl)
    assertEquals("2026-03-29T12:00:00Z", entity.publishedAt)
    assertEquals("Full content", entity.content)
  }
}
