package seifemadhamdy.buzzi.presentation.articles

import seifemadhamdy.buzzi.domain.model.Article

data class ArticlesUiState(
    val articles: List<Article> = emptyList(),
    val query: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface ArticlesIntent {
    data object LoadArticles : ArticlesIntent
    data class SearchQueryChanged(val query: String) : ArticlesIntent
    data object Retry : ArticlesIntent
    data class ArticleSelected(val article: Article) : ArticlesIntent
}

sealed interface ArticlesEffect {
    data class NavigateToDetails(val article: Article) : ArticlesEffect
}