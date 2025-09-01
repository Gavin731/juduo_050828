package com.film.television.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitNetwork {

    private val contentRetrofit = Retrofit.Builder()
        .baseUrl(ConnectionConstants.CONTENT_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val contentService: ContentService by lazy {
        contentRetrofit.create(ContentService::class.java)
    }
}