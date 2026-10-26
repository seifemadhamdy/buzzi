package seifemadhamdy.buzzi.domain.usecase

import javax.inject.Inject
import seifemadhamdy.buzzi.domain.model.Article
import seifemadhamdy.buzzi.domain.repository.ArticleRepository

class SearchArticlesUseCase @Inject constructor(private val repository: ArticleRepository) {
  suspend operator fun invoke(query: String): List<Article> = repository.searchArticles(query)
}
