package com.example.finalexammovieapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.finalexammovieapp.databinding.ItemMovieBinding
import kotlin.random.Random

class MovieAdapter(
    private var movies: List<Movie>,
    private val onClick: (Movie) -> Unit
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    inner class MovieViewHolder(val binding: ItemMovieBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        with(holder.binding) {
            tvTitle.text = movie.title ?: "Unknown Movie"

            // Randomize Year just for display if missing
            val displayYear = if (movie.year == 0 || movie.year == 2024) Random.nextInt(1990, 2023) else movie.year
            tvYear.text = displayYear.toString()

            // UPDATED: Just display the rating we saved in ListFragment
            tvRating.text = "Rating: ${movie.rating}"

            Glide.with(root.context)
                .load(movie.poster)
                .centerCrop()
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(ivMoviePoster)

            root.setOnClickListener { onClick(movie) }
        }
    }

    override fun getItemCount() = movies.size

    fun updateData(newMovies: List<Movie>) {
        movies = newMovies
        notifyDataSetChanged()
    }
}