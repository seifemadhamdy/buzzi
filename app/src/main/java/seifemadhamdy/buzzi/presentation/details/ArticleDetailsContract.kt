package seifemadhamdy.buzzi.presentation.details

import seifemadhamdy.buzzi.domain.model.Article

data class ArticleDetailsUiState(
    val article: Article? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)
