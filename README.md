![Recording 2025-12-29 180243](https://github.com/user-attachments/assets/61526eac-3ce4-4446-a897-bf418d5bfe2c)



```package com.example.finalexammovieapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.finalexammovieapp.databinding.FragmentDetailBinding

// ეს არის დეტალების ფრაგმენტი, რომელიც აჩვენებს ფილმის სრულ ინფორმაციას
class DetailFragment : Fragment(R.layout.fragment_detail) {

    // ვიღებთ არგუმენტებს (კონკრეტულ ფილმს/Movie ობიექტს), რომელიც გადმოგვეცა წინა ეკრანიდან
    private val args: DetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // ViewBinding-ის ინიციალიზაცია, რათა XML ელემენტებს (ღილაკებს, ტექსტებს) დავუკავშირდეთ
        val binding = FragmentDetailBinding.bind(view)
        
        // ვიღებთ ფილმის მონაცემებს არგუმენტებიდან
        val movie = args.movieData

        // სათაურის დაყენება: თუ მონაცემი ცარიელია (null), გამოჩნდება "Unknown Title"
        val title = movie.title ?: "Unknown Title"
        binding.tvDetailTitle.text = title
        
        // ჟანრის დაყენება
        binding.tvDetailGenre.text = movie.genre?.joinToString(", ") ?: "Animation, Classic"

        // სიუჟეტის (Plot) ლოგიკა: თუ აღწერა არ მოყვა API-დან, ვწერთ ჩვენს ნაგულისხმევ ტექსტს
        val plotText = if (movie.plot == null || movie.plot.length < 50) {
            "This is one of the most celebrated animated movies of its time. " +
                    "Featuring ground-breaking visuals and a heartwarming story, it captured the hearts of audiences worldwide."
        } else {
            movie.plot
        }
        binding.tvDetailPlot.text = plotText

        // Glide-ის გამოყენება სურათის ჩასატვირთად URL-იდან
        Glide.with(this)
            .load(movie.poster)
            .placeholder(android.R.drawable.ic_menu_gallery) // დროებითი სურათი ჩატვირთვის დროს
            .into(binding.ivDetailPoster)

        // "გაზიარების" (Share) ღილაკის ლოგიკა
        binding.btnShare.setOnClickListener {
            // ვქმნით Intent-ს, რათა ინფორმაცია გავუგზავნოთ სხვა აპლიკაციებს (მაგ. Messenger, WhatsApp)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Check out this movie!")
                putExtra(Intent.EXTRA_TEXT, "I found this movie in my app:\n\n$title\n\n$plotText")
            }
            // ხსნის სისტემურ ფანჯარას აპლიკაციის ასარჩევად
            startActivity(Intent.createChooser(shareIntent, "Share via"))
        }

        // "Google-ში ძებნის" ღილაკი
        binding.btnBrowser.setOnClickListener {
            // ვქმნით ბმულს ფილმის სათაურის მიხედვით
            val url = "https://www.google.com/search?q=$title movie"
            
            // ვქმნით Intent-ს, რომელიც გახსნის ბრაუზერს ამ ბმულზე
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(browserIntent)
        }
    }
}```
