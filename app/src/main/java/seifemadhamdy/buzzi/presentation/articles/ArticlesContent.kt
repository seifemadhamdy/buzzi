package seifemadhamdy.buzzi.presentation.articles

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.Newspaper
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import seifemadhamdy.buzzi.R
import seifemadhamdy.buzzi.domain.model.Article
import seifemadhamdy.buzzi.presentation.articles.components.ArticleCard
import seifemadhamdy.buzzi.ui.theme.BuzziTheme

private val ScreenPadding = 16.dp

private val TextInset = 8.dp

private val ListTopPadding = 24.dp

@Composable
fun ArticlesContent(
  uiState: ArticlesUiState,
  onSearchQueryChanged: (String) -> Unit,
  onRetry: () -> Unit,
  onArticleSelected: (Article) -> Unit,
  modifier: Modifier = Modifier,
) {
  val errorMessage = uiState.errorMessage
  val hasArticles = uiState.articles.isNotEmpty()

  var headerHeightPx by remember { mutableIntStateOf(0) }
  val headerHeight = with(LocalDensity.current) { headerHeightPx.toDp() }

  Scaffold(modifier = modifier) { innerPadding ->
    Box(modifier = Modifier.fillMaxSize()) {
      Box(
        modifier =
          Modifier.fillMaxSize()
            .then(if (hasArticles) Modifier else Modifier.padding(top = headerHeight)),
        contentAlignment = Alignment.Center,
      ) {
        when {
          uiState.isLoading && !hasArticles -> {
            CircularProgressIndicator()
          }

          errorMessage != null && !hasArticles -> {
            StatusMessage(
              icon = Icons.Rounded.CloudOff,
              title = "Couldn't load headlines",
              message = errorMessage,
              iconContainerColor = MaterialTheme.colorScheme.errorContainer,
              iconColor = MaterialTheme.colorScheme.onErrorContainer,
              actionLabel = "Try again",
              onAction = onRetry,
            )
          }

          !hasArticles && uiState.query.isNotBlank() -> {
            StatusMessage(
              icon = Icons.Rounded.SearchOff,
              title = "No results",
              message =
                "Nothing in the top headlines matches " +
                  "“${uiState.query.trim()}”. Try a different keyword.",
              actionLabel = "Clear search",
              actionIcon = Icons.Rounded.Close,
              onAction = { onSearchQueryChanged("") },
            )
          }

          !hasArticles -> {
            StatusMessage(
              icon = Icons.Rounded.Newspaper,
              title = "No headlines yet",
              message = "There are no top headlines right now. " + "Check back in a little while.",
              actionLabel = "Refresh",
              onAction = onRetry,
            )
          }

          else -> {
            LazyColumn(
              modifier = Modifier.fillMaxSize(),
              contentPadding =
                PaddingValues(
                  start = ScreenPadding,
                  end = ScreenPadding,
                  top = headerHeight + ListTopPadding,
                  bottom = innerPadding.calculateBottomPadding() + ScreenPadding,
                ),
              verticalArrangement = Arrangement.spacedBy(ScreenPadding),
            ) {
              item(key = "section_title") {
                Text(
                  text = "Top Headlines",
                  style = MaterialTheme.typography.titleLarge,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurface,
                  modifier = Modifier.padding(horizontal = TextInset),
                )
              }

              items(
                items = uiState.articles,
                key = { article -> article.url },
              ) { article ->
                ArticleCard(
                  article = article,
                  onClick = {
                    onArticleSelected(article)
                  },
                )
              }
            }
          }
        }
      }

      Header(
        query = uiState.query,
        isSearching = uiState.isLoading && hasArticles,
        onQueryChange = onSearchQueryChanged,
        modifier =
          Modifier.align(Alignment.TopCenter)
            .onSizeChanged { headerHeightPx = it.height }
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.88f))
            .padding(top = innerPadding.calculateTopPadding()),
      )
    }
  }
}

@Composable
private fun Header(
  query: String,
  isSearching: Boolean,
  onQueryChange: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier =
      modifier
        .fillMaxWidth()
        .padding(
          start = ScreenPadding,
          end = ScreenPadding,
          top = 12.dp,
          bottom = 12.dp,
        )
  ) {
    Text(
      text = stringResource(R.string.app_name),
      style = MaterialTheme.typography.displaySmall,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.onSurface,
      modifier = Modifier.padding(horizontal = TextInset),
    )

    Spacer(Modifier.height(20.dp))

    SearchField(
      query = query,
      isSearching = isSearching,
      onQueryChange = onQueryChange,
    )
  }
}

@Composable
private fun SearchField(
  query: String,
  isSearching: Boolean,
  onQueryChange: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val focusManager = LocalFocusManager.current

  TextField(
    value = query,
    onValueChange = onQueryChange,
    modifier = modifier.fillMaxWidth(),
    placeholder = { Text("Search top headlines") },
    leadingIcon = {
      Icon(
        imageVector = Icons.Rounded.Search,
        contentDescription = null,
      )
    },
    trailingIcon = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        if (isSearching) {
          CircularProgressIndicator(
            modifier = Modifier.size(20.dp),
            strokeWidth = 2.dp,
          )
        }

        if (query.isNotEmpty()) {
          IconButton(onClick = { onQueryChange("") }) {
            Icon(
              imageVector = Icons.Rounded.Close,
              contentDescription = "Clear search",
            )
          }
        } else if (isSearching) {
          Spacer(Modifier.width(16.dp))
        }
      }
    },
    singleLine = true,
    shape = CircleShape,
    colors =
      TextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent,
      ),
    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
  )
}

@Composable
private fun StatusMessage(
  icon: ImageVector,
  title: String,
  message: String,
  actionLabel: String,
  onAction: () -> Unit,
  modifier: Modifier = Modifier,
  actionIcon: ImageVector = Icons.Rounded.Refresh,
  iconContainerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
  iconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
  Column(
    modifier = modifier.fillMaxWidth().padding(start = 40.dp, end = 40.dp, bottom = 48.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Surface(
      shape = CircleShape,
      color = iconContainerColor,
      contentColor = iconColor,
      modifier = Modifier.size(96.dp),
    ) {
      Box(contentAlignment = Alignment.Center) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          modifier = Modifier.size(44.dp),
        )
      }
    }

    Spacer(Modifier.height(24.dp))

    Text(
      text = title,
      style = MaterialTheme.typography.titleLarge,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.onSurface,
      textAlign = TextAlign.Center,
    )

    Spacer(Modifier.height(8.dp))

    Text(
      text = message,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center,
    )

    Spacer(Modifier.height(24.dp))

    FilledTonalButton(
      onClick = onAction,
      contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
    ) {
      Icon(
        imageVector = actionIcon,
        contentDescription = null,
        modifier = Modifier.size(ButtonDefaults.IconSize),
      )

      Spacer(Modifier.size(ButtonDefaults.IconSpacing))

      Text(actionLabel)
    }
  }
}

private val previewArticles =
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

@Preview(showBackground = true)
@Composable
private fun ArticlesContentPreview() {
  BuzziTheme {
    ArticlesContent(
      uiState = ArticlesUiState(articles = previewArticles),
      onSearchQueryChanged = {},
      onRetry = {},
      onArticleSelected = {},
    )
  }
}

@Preview(showBackground = true)
@Composable
private fun ArticlesContentSearchingPreview() {
  BuzziTheme {
    ArticlesContent(
      uiState =
        ArticlesUiState(
          query = "markets",
          isLoading = true,
          articles = previewArticles,
        ),
      onSearchQueryChanged = {},
      onRetry = {},
      onArticleSelected = {},
    )
  }
}

@Preview(showBackground = true)
@Composable
private fun ArticlesContentLoadingPreview() {
  BuzziTheme {
    ArticlesContent(
      uiState = ArticlesUiState(isLoading = true),
      onSearchQueryChanged = {},
      onRetry = {},
      onArticleSelected = {},
    )
  }
}

@Preview(showBackground = true)
@Composable
private fun ArticlesContentErrorPreview() {
  BuzziTheme {
    ArticlesContent(
      uiState = ArticlesUiState(errorMessage = "Check your internet connection and try again."),
      onSearchQueryChanged = {},
      onRetry = {},
      onArticleSelected = {},
    )
  }
}

@Preview(showBackground = true)
@Composable
private fun ArticlesContentEmptyPreview() {
  BuzziTheme {
    ArticlesContent(
      uiState = ArticlesUiState(),
      onSearchQueryChanged = {},
      onRetry = {},
      onArticleSelected = {},
    )
  }
}

@Preview(showBackground = true)
@Composable
private fun ArticlesContentNoResultsPreview() {
  BuzziTheme {
    ArticlesContent(
      uiState = ArticlesUiState(query = "quantum llamas"),
      onSearchQueryChanged = {},
      onRetry = {},
      onArticleSelected = {},
    )
  }
}
