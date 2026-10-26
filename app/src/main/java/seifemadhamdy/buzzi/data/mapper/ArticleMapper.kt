package seifemadhamdy.buzzi.data.mapper

import seifemadhamdy.buzzi.data.remote.dto.ArticleDto
import seifemadhamdy.buzzi.domain.model.Article

fun ArticleDto.toDomainOrNull(): Article? {
    val validTitle = title
        ?.trim()
        ?.takeIf { it.isNotEmpty() }
        ?: return null

    val validUrl = url
        ?.trim()
        ?.takeIf { it.isNotEmpty() }
        ?: return null

    return Article(
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