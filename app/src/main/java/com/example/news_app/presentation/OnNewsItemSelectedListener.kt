package com.example.news_app.presentation

import com.example.news_app.domain.NewsItemModel

interface OnNewsItemSelectedListener {
    fun onNewsItemSelected(newsItem: NewsItemModel)
}