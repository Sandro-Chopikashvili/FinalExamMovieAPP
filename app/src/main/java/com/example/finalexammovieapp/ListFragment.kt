package com.example.finalexammovieapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalexammovieapp.databinding.FragmentListBinding
import kotlinx.coroutines.launch
import kotlin.random.Random

class ListFragment : Fragment(R.layout.fragment_list) {

    private lateinit var binding: FragmentListBinding
    private lateinit var adapter: MovieAdapter

    private var allMovies: List<Movie> = emptyList()

    // Track sort states
    private var isNameAscending = true
    private var isRatingHighToLow = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentListBinding.bind(view)

        setupRecyclerView()
        setupSearch()
        setupButtons()
        fetchMovies()
    }

    private fun setupRecyclerView() {
        adapter = MovieAdapter(emptyList()) { movie ->
            val action = ListFragmentDirections.actionListFragmentToDetailFragment(movie)
            findNavController().navigate(action)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun setupButtons() {
        // 1. NAME SORT
        binding.btnSortName.setOnClickListener {
            isNameAscending = !isNameAscending

            val sorted = if (isNameAscending) {
                binding.btnSortName.text = "Sort Name (A-Z)"
                allMovies.sortedBy { it.title }
            } else {
                binding.btnSortName.text = "Sort Name (Z-A)"
                allMovies.sortedByDescending { it.title }
            }
            // Important: update the master list sequence so subsequent filters work
            allMovies = sorted
            adapter.updateData(sorted)
        }

        // 2. RATING SORT
        binding.btnSortRating.setOnClickListener {
            isRatingHighToLow = !isRatingHighToLow

            val sorted = if (isRatingHighToLow) {
                binding.btnSortRating.text = "Sort Rating (High)"
                allMovies.sortedByDescending { it.rating ?: 0.0 }
            } else {
                binding.btnSortRating.text = "Sort Rating (Low)"
                allMovies.sortedBy { it.rating ?: 0.0 }
            }
            allMovies = sorted
            adapter.updateData(sorted)
        }
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })
    }

    private fun filterList(query: String?) {
        val searchText = query?.lowercase() ?: ""
        if (searchText.isEmpty()) {
            adapter.updateData(allMovies)
        } else {
            val filteredList = allMovies.filter { movie ->
                (movie.title?.lowercase()?.contains(searchText) == true)
            }
            adapter.updateData(filteredList)
        }
    }

    private fun fetchMovies() {
        lifecycleScope.launch {
            try {
                val movies = RetrofitClient.instance.getMovies()

                // CRITICAL FIX: Assign random ratings HERE, once and for all.
                movies.forEach { movie ->
                    // Generate a random rating between 7.0 and 9.9
                    val randomRating = (70..99).random() / 10.0
                    movie.rating = randomRating
                }

                allMovies = movies

                if (movies.isNotEmpty()) {
                    adapter.updateData(movies)
                } else {
                    Toast.makeText(context, "API returned empty list", Toast.LENGTH_SHORT).show()
                }
                binding.progressBar.visibility = View.GONE

            } catch (e: Exception) {
                Log.e("ListFragment", "Network Error: ${e.message}")
                Toast.makeText(context, "Network Error! Loading Backup Data...", Toast.LENGTH_LONG).show()

                val backupData = listOf(
                    Movie(1, "Test Movie (Offline)", "https://via.placeholder.com/150", 2024, listOf("Action", "Demo"), 9.9, "Offline Mode Active."),
                    Movie(2, "Second Movie", "https://via.placeholder.com/150", 2023, listOf("Comedy"), 8.5, "RecyclerView is working."),
                    Movie(3, "Another One", "https://via.placeholder.com/150", 2022, listOf("Drama"), 7.5, "More data for sorting.")
                )
                allMovies = backupData
                adapter.updateData(backupData)
                binding.progressBar.visibility = View.GONE
            }
        }
    }
}