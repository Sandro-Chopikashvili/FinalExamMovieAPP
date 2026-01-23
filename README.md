<p align="center">
  <img src="https://github.com/user-attachments/assets/61526eac-3ce4-4446-a897-bf418d5bfe2c" width="300">
</p>









## ListFragment.kt – ძირითადი ფუნქციონალი

`ListFragment` პასუხისმგებელია ფილმების სიის ჩატვირთვაზე, ჩვენებაზე და მარტივ ფილტრაცია/სორტირებაზე.

### ძირითადი მახასიათებლები

- **მონაცემთა წყარო**  
  იყენებს **RetrofitClient**-ს API-დან ფილმების წამოსაღებად

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
}```
