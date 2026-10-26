package seifemadhamdy.buzzi.domain.usecase

import seifemadhamdy.buzzi.domain.model.Article
import seifemadhamdy.buzzi.domain.repository.ArticleRepository
import javax.inject.Inject

class GetTopHeadlinesUseCase @Inject constructor(
    private val repository: ArticleRepository,
) {
    suspend operator fun invoke(): List<Article> =
        repository.getTopHeadlines()
}