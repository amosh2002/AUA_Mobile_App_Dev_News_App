package com.example.news_app.data

enum class Status(private val text: String) {

    OK("ok"),
    LOADING("loading"),
    ERROR("error");

    override fun toString(): String = text
}