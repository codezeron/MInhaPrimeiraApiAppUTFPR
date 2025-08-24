package com.example.minhaprimeiraapiapputfpr.service

import com.example.minhaprimeiraapiapputfpr.model.Item
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("items") suspend fun getItems(): List<Item>

    @GET("items/{id}") suspend fun getItemById(@Path("id") id: String): Item

    @DELETE("items/{id}") suspend fun deleteItemById(@Path("id") id: String)
}