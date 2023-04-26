package com.example.news_app.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface NewsApiService {
    @GET("/$V2/$TOP_HEADLINES")
    suspend fun fetchNews(
        @Query("country") country: String,
        @Query("apiKey") apiKey: String = API_KEY_VALUE
    ): Response<NewsResponse>

    @GET("/$V2/$TOP_HEADLINES")
    suspend fun fetchNews(
        @Query("country") country: String,
        @Query("category") category: String,
        @Query("apiKey") apiKey: String = API_KEY_VALUE
    ): Response<NewsResponse>
}
