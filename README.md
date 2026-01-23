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
    
- **Navigation**
  ნავიგაცია: როდესაც ფილმს აჭერთ, ის მიმართავს Navigation კომპონენტს, რომ გადავიდეს DetailFragment-ზე და თან ატანს კონკრეტულ Movie ობიექტს.

  

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





## DetailFragment.kt - ძირითადი ფუნქციონალი

- მონაცემთა დამუშავება: იღებს Movie ობიექტს Navigation Arguments-ის საშუალებით.
- UI ლოგიკა: იყენებს Glide-ს სურათის ჩასატვირთად და ამუშავებს ლოგიკას ფილმის აღწერის (plot) არარსებობის შემთხვევაში (აჩვენებს ავტომატურ ტექსტს).
- მომხმარებლის ინტერაქცია: იყენებს Implicit Intents-ს, რათა მომხმარებელს შეეძლოს ინფორმაციის გაზიარება (Share) ან ფილმის Google-ში მოძებნა.
- მომხმარებლის ინტერაქცია: იყენებს Implicit Intents-ს, რათა მომხმარებელს შეეძლოს ინფორმაციის გაზიარება (Share) ან ფილმის Google-ში მოძებნა.
  
```kotlin
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
```

