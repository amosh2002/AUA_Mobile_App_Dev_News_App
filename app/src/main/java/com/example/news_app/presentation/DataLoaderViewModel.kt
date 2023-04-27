package com.example.news_app.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.news_app.data.NewsRepo
import com.example.news_app.data.Status
import com.example.news_app.domain.NewsItemModel
import kotlinx.coroutines.launch

class DataLoaderViewModel : ViewModel() {

    private var _status = MutableLiveData(Status.LOADING)
    private val _newsList: MutableLiveData<List<NewsItemModel>> = MutableLiveData()
    val status: LiveData<Status> = _status
    val newsList: LiveData<List<NewsItemModel>> = _newsList

    fun loadNews(category: String? = null, searchQuery: String? = null) {
        viewModelScope.launch {
            val loadedItems = NewsRepo().loadNews(category = category, searchQuery = searchQuery)
            val loadedNews = loadedItems.newsItemModels
            val loadedStatus = loadedItems.status
            if (loadedStatus.isNotBlank()) {
                _status.postValue(Status.LOADING)
                _newsList.postValue(
                    loadedNews
                )
                _status.postValue(Status.OK)
            } else {
                _status.postValue(Status.ERROR)
            }
        }
    }
}