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

    private val _status = MutableLiveData(Status.LOADING)
    private val _newsList: MutableLiveData<List<NewsItemModel>> = MutableLiveData()
    val status: LiveData<Status> = _status
    val newsList: LiveData<List<NewsItemModel>> = _newsList

    fun loadNews() {
        viewModelScope.launch {
            val loadedNews = NewsRepo().loadNews()
            if (loadedNews.isNotEmpty()) {
                _status.value = Status.LOADING
                _newsList.postValue(
                    loadedNews
                )
                _status.value = Status.SUCCESS
            } else {
                _status.value = Status.ERROR
            }
        }
    }
}