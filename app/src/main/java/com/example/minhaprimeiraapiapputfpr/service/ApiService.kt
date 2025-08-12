package com.example.minhaprimeiraapiapputfpr.service

import com.example.minhaprimeiraapiapputfpr.model.Item
import retrofit2.http.GET

interface ApiService {

    @GET("items") suspend fun getItems(): List<Item>
}