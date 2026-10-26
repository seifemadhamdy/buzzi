package seifemadhamdy.buzzi.data.remote

import retrofit2.http.GET
import retrofit2.http.Query
import seifemadhamdy.buzzi.data.remote.dto.NewsResponseDto

interface NewsApiService {
  @GET("top-headlines")
  suspend fun getTopHeadlines(
    @Query("country") country: String = DEFAULT_COUNTRY,
    @Query("page") page: Int,
  ): NewsResponseDto

  @GET("top-headlines")
  suspend fun searchTopHeadlines(
    @Query("country") country: String = DEFAULT_COUNTRY,
    @Query("q") query: String,
  ): NewsResponseDto

  private companion object {
    const val DEFAULT_COUNTRY = "us"
  }
}
