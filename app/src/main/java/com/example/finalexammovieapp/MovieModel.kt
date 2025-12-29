package com.example.finalexammovieapp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import com.google.gson.annotations.SerializedName

@Parcelize
data class Movie(
    val id: Int? = 0,
    val title: String? = "Unknown Title",
    @SerializedName("posterURL")
    val poster: String? = "",
    val year: Int? = 2024,
    val genre: List<String>? = listOf("Classic"),

    // CHANGE THIS LINE: 'val' -> 'var'
    var rating: Double? = 0.0,

    val plot: String? = "Description not available for this title."
) : Parcelable