/*
 * Copyright (C) 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.news_app.data

import com.example.news_app.domain.NewsItemModel
import com.example.news_app.domain.ResponseModel

class NewsRepo {
    suspend fun loadNews(): ResponseModel {
        val response = RetrofitHelper.getInstance()
            .create(NewsApiService::class.java)
            .fetchNews("us")
        val newsItemModel = response.run {
            this.body()?.articles?.map {
                NewsItemModel(
                    it.Source?.name ?: "",
                    it.author ?: "",
                    it.title ?: "",
                    it.urlToImage ?: ""
                )
            } ?: listOf()
        }
        val status = response.body()?.status

        return ResponseModel(status = status!!, newsItemModels = newsItemModel)
    }
}