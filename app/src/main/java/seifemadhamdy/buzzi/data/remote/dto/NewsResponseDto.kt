package seifemadhamdy.buzzi.data.remote.dto

data class NewsResponseDto(
    val status: String? = null,
    val totalResults: Int? = null,
    val articles: List<ArticleDto>? = null,
    val code: String? = null,
    val message: String? = null
)