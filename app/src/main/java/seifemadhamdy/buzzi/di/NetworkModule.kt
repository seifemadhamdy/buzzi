package seifemadhamdy.buzzi.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import seifemadhamdy.buzzi.BuildConfig
import seifemadhamdy.buzzi.data.remote.NewsApiService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
  @Provides
  @Singleton
  fun provideOkHttpClient(): OkHttpClient =
    OkHttpClient.Builder()
      .addInterceptor { chain ->
        val authenticatedRequest =
          chain.request().newBuilder().header("x-api-key", BuildConfig.NEWS_API_KEY).build()
        chain.proceed(authenticatedRequest)
      }
      .build()

  @Provides
  @Singleton
  fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
    Retrofit.Builder()
      .baseUrl("https://newsapi.org/v2/")
      .client(okHttpClient)
      .addConverterFactory(GsonConverterFactory.create())
      .build()

  @Provides
  @Singleton
  fun provideNewsApiService(retrofit: Retrofit): NewsApiService =
    retrofit.create(NewsApiService::class.java)
}
