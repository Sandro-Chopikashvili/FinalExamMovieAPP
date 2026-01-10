package com.example.finalexammovieapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.finalexammovieapp.databinding.FragmentDetailBinding

class DetailFragment : Fragment(R.layout.fragment_detail) {

    private val args: DetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentDetailBinding.bind(view)
        val movie = args.movieData

        val title = movie.title ?: "Unknown Title"
        binding.tvDetailTitle.text = title
        binding.tvDetailGenre.text = movie.genre?.joinToString(", ") ?: "Animation, Classic"

        val plotText = if (movie.plot == null || movie.plot.length < 50) {
            "This is one of the most celebrated animated movies of its time. " +
                    "Featuring ground-breaking visuals and a heartwarming story, it captured the hearts of audiences worldwide."
        } else {
            movie.plot
        }
        binding.tvDetailPlot.text = plotText

        Glide.with(this)
            .load(movie.poster)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .into(binding.ivDetailPoster)

        binding.btnShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Check out this movie!")
                putExtra(Intent.EXTRA_TEXT, "I found this movie in my app:\n\n$title\n\n$plotText")
            }
            startActivity(Intent.createChooser(shareIntent, "Share via"))
        }

        binding.btnBrowser.setOnClickListener {
            val url = "https://www.google.com/search?q=$title movie"
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(browserIntent)
        }
    }
}