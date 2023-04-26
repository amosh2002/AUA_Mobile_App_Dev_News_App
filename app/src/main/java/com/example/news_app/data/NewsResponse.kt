package com.example.news_app.data

import com.google.gson.annotations.SerializedName

data class NewsResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("totalResults")
    val totalResults: String?,
    @SerializedName("articles")
    val articles: List<ArticleResponse>?
)

data class ArticleResponse(
    @SerializedName("source")
    val Source: Source?,

    @SerializedName("author")
    val author: String?,

    @SerializedName("title")
    val title: String?,

    @SerializedName("urlToImage")
    val urlToImage: String?,

    @SerializedName("description")
    val description: String?
)

data class Source(
    @SerializedName("name")
    val name: String?
)
