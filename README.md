<p align="center">
  <img src="https://github.com/user-attachments/assets/61526eac-3ce4-4446-a897-bf418d5bfe2c" width="300">
</p>





ListFragment.kt:
•	იყენებს RetrofitClient, რათა წამოიღოს მონაცემები ინტერნეტიდან
•	თუ ინტერნეტი გაითიშება იყენებს "Backup Data", რათა აპლიკაცია არ გაიქრაშოს
•	იყენებს RecyclerView და აკავშირებს MovieAdapter, რათა გამოსახოს ფილმები ეკრანზე
  ღილაკები:
    სორტირება: 


private fun setupButtons() {
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


