package com.example.finalexammovieapp

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiService {
    @GET("animation")
    suspend fun getMovies(): List<Movie>
}

object RetrofitClient {
    private const val BASE_URL = "https://api.sampleapis.com/movies/"

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}