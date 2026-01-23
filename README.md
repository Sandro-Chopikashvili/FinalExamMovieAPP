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


## MovieAdapter.kt - ძირითადი ფუნქციონალი

1. *კლასის პარამეტრები (Constructor)*
   ```kotlin
   private var movies: List<Movie>,
   private val onClick: (Movie) -> Unit
   ```
- *movies:* ეს არის მონაცემების სია, რომელიც ადაპტერმა უნდა გამოაჩინოს.
- *onClick:* ეს არის ფუნქცია, რომელიც გამოიძახება მაშინ, როდესაც მომხმარებელი კონკრეტულ ფილმს დააჭერს

2. *onCreateViewHolder*
   - ეს ფუნქცია ეშვება მაშინ, როდესაც ეკრანს სჭირდება ახალი ელემენტის შექმნა.

3. *onBindViewHolder*
   ეს ფუნქცია ეშვება თითოეული ფილმისთვის. ის აკავშირებს კონკრეტულ მონაცემებს ვიზუალურ ელემენტებთან:
   - სათაური: tvTitle.text = movie.title (ანიჭებს ფილმის სახელს).
   - წელი (Random Logic) კოდში წერია ლოგიკა: თუ წელი არასწორია (0) ან არის 2027, ის ირჩევს შემთხვევით წელს 1990-დან 2026-მდე, რათა სია უფრო მრავალფეროვანი გამოჩნდეს.
   - რეიტინგი: tvRating.text - აჩვენებს იმ რეიტინგს, რომელიც ListFragment-ში მივანიჭეთ.
     
   - სურათი (Glide):
     
```kotlin
     Glide.with(root.context)
    .load(movie.poster) // ტვირთავს ფოტოს ლინკიდან
    .into(ivMoviePoster) // სვამს სურათს ImageView-ში
```
     
    *დაკლიკება: root.setOnClickListener { onClick(movie) } — როცა ფილმს დააჭერ, ის ატყობინებს ListFragment-ს, რომელ ფილმზე მოხდა დაჭერა.*

```kotlin
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

            val displayYear = if (movie.year == 0 || movie.year == 2027) Random.nextInt(1990, 2026) else movie.year
            tvYear.text = displayYear.toString()

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
```


## MovieModel.kt 
- განსაზღვრავს სტრუქტურას: ეუბნება აპლიკაციას, რომ ყოველ "ფილმს" აუცილებლად უნდა ჰქონდეს სათაური, სურათის ლინკი, წელი და რეიტინგი.
  
- თარგმნის მონაცემებს: გარდაქმნის ინტერნეტიდან (JSON) მოსულ ნედლ ინფორმაციას კოტლინის ობიექტებად, რომელთა წაკითხვაც შენს აპლიკაციას შეუძლია.
  
- ტრანსპორტირება: ფუთავს მონაცემებს პაკეტად (Parcelable), რათა შეგეძლოს ფილმის დეტალების გადაგზავნა მთავარი ეკრანიდან დეტალურ ეკრანზე, როდესაც მომხმარებელი ფილმს დააჭერს.


## RetrofitClient.kt
- *მიზანი:* მართავს ინტერნეტ კავშირს და API-დან ინფორმაციის წამოღებას.
- *მთავარი კომპონენტები:*
    - *Retrofit Builder:* აკონფიგურირებს მთავარ ბმულს (Base URL) და JSON-ის გადამყვანს (Gson).
    - *Singleton Pattern:* იყენებს object-ს, რათა შეიქმნას კლიენტის მხოლოდ ერთი ეგზემპლარი და დაიზოგოს რესურსები.
    - *Coroutines Support:* იყენებს ფუნქციებს, რათა მონაცემების წამოღება მოხდეს ფონურ რეჟიმში და ეკრანი არ გაიყინოს.
