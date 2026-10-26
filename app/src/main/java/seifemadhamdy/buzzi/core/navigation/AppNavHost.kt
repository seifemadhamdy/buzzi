package seifemadhamdy.buzzi.core.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import seifemadhamdy.buzzi.presentation.articles.ArticlesScreen
import seifemadhamdy.buzzi.presentation.details.ArticleDetailsRoute

private object Routes {
    const val ARTICLES = "articles"
    const val DETAILS = "details/{url}"
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.ARTICLES,
    ) {
        composable(Routes.ARTICLES) {
            ArticlesScreen(
                onNavigateToDetails = { article ->
                    navController.navigate(
                        "details/${Uri.encode(article.url)}"
                    )
                },
            )
        }

        composable(
            route = Routes.DETAILS,
            arguments = listOf(
                navArgument("url") {
                    type = NavType.StringType
                },
            ),
        ) {
            ArticleDetailsRoute(
                onBack = {
                    navController.popBackStack()
                },
            )
        }
    }
}