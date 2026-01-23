<p align="center">
  <img src="https://github.com/user-attachments/assets/61526eac-3ce4-4446-a897-bf418d5bfe2c" width="300">
</p>









## ListFragment.kt – ძირითადი ფუნქციონალი

`ListFragment` პასუხისმგებელია ფილმების სიის ჩატვირთვაზე, ჩვენებაზე და მარტივ ფილტრაცია/სორტირებაზე.

### ძირითადი მახასიათებლები

- **მონაცემთა წყარო**  
  იყენებს **RetrofitClient**-ს API-დან ფილმების წამოსაღებად
  ```kotlin
      private fun fetchMovies() {
        lifecycleScope.launch {
            try {
                val movies = RetrofitClient.instance.getMovies()

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
```

- **Offline რეჟიმი**  
  თუ ინტერნეტი გათიშულია, იყენებს **"Backup Data"**-ს → აპლიკაცია არ კრაშდება

- **UI**  
  - **RecyclerView** + **MovieAdapter** ფილმების სიის გამოსახულად  
  - ძებნა (**SearchView**)  
  - სორტირების ღილაკები (სახელითა და რეიტინგით)

### სორტირების ლოგიკა

```kotlin
private fun setupButtons() {
    // სორტირება სახელით (A-Z / Z-A)
    binding.btnSortName.setOnClickListener {
        isNameAscending = !isNameAscending
        val sorted = if (isNameAscending) {
            binding.btnSortName.text = "Sort Name (A-Z)"
            allMovies.sortedBy { it.title }
        } else {
            binding.btnSortName.text = "Sort Name (Z-A)"
            allMovies.sortedByDescending { it.title }
        }
        allMovies = sorted
        adapter.updateData(sorted)
    }

    // სორტირება რეიტინგით (მაღალი → დაბალი / დაბალი → მაღალი)
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
```

  ### ძებნის ლოგიკა

  ```kotlin
    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })
    }
  ```
