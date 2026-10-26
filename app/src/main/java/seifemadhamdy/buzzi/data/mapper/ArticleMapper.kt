package seifemadhamdy.buzzi.data.mapper

import seifemadhamdy.buzzi.data.local.entity.ArticleEntity
import seifemadhamdy.buzzi.data.remote.dto.ArticleDto
import seifemadhamdy.buzzi.domain.model.Article

fun ArticleDto.toEntityOrNull(): ArticleEntity? {
    val validTitle = title
        ?.trim()
        ?.takeIf { it.isNotEmpty() }
        ?: return null

    val validUrl = url
        ?.trim()
        ?.takeIf { it.isNotEmpty() }
        ?: return null

    return ArticleEntity(
        sourceName = source?.name,
        author = author,
        title = validTitle,
        description = description,
        url = validUrl,
        imageUrl = urlToImage,
        publishedAt = publishedAt,
        content = content,
    )
}

fun ArticleEntity.toDomain(): Article =
    Article(
        sourceName = sourceName,
        author = author,
        title = title,
        description = description,
        url = url,
        imageUrl = imageUrl,
        publishedAt = publishedAt,
        content = content,
    )

fun Article.toEntity(): ArticleEntity =
    ArticleEntity(
        sourceName = sourceName,
        author = author,
        title = title,
        description = description,
        url = url,
        imageUrl = imageUrl,
        publishedAt = publishedAt,
        content = content,
    )