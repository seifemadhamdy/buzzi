package seifemadhamdy.buzzi.presentation.articles

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import seifemadhamdy.buzzi.domain.model.Article
import seifemadhamdy.buzzi.ui.theme.BuzziTheme

@Composable
fun ArticlesScreen(
  onNavigateToDetails: (Article) -> Unit,
  viewModel: ArticlesViewModel = hiltViewModel(),
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(viewModel) {
    viewModel.onIntent(ArticlesIntent.LoadArticles)
  }

  LaunchedEffect(viewModel) {
    viewModel.effects.collect { effect ->
      when (effect) {
        is ArticlesEffect.NavigateToDetails -> onNavigateToDetails(effect.article)
      }
    }
  }

  ArticlesContent(
    uiState = uiState,
    onSearchQueryChanged = { query ->
      viewModel.onIntent(ArticlesIntent.SearchQueryChanged(query))
    },
    onRetry = {
      viewModel.onIntent(ArticlesIntent.Retry)
    },
    onArticleSelected = { article ->
      viewModel.onIntent(ArticlesIntent.ArticleSelected(article))
    },
  )
}

@Preview(showBackground = true)
@Composable
private fun ArticlesScreenPreview() {
  BuzziTheme {
    ArticlesContent(
      uiState =
        ArticlesUiState(
          articles =
            listOf(
              Article(
                sourceName = "BBC News",
                author = "Jane Smith",
                title = "Global technology companies announce new innovations",
                description = "A summary of today's technology news.",
                url = "https://example.com/technology",
                imageUrl = null,
                publishedAt = "2026-10-26T10:00:00Z",
                content = null,
              ),
              Article(
                sourceName = "Reuters",
                author = null,
                title = "Markets react to the latest economic developments",
                description = "Investors assess the latest market news.",
                url = "https://example.com/markets",
                imageUrl = null,
                publishedAt = "2026-10-26T08:15:00Z",
                content = null,
              ),
            )
        ),
      onSearchQueryChanged = {},
      onRetry = {},
      onArticleSelected = {},
    )
  }
}
