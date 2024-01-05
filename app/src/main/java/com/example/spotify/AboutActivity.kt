package com.example.spotify

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.spotify.databinding.ActivityAboutBinding

private lateinit var binding: ActivityAboutBinding
class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Spotify)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Settings"
        binding.tvAbout.text = aboutText()
    }
    private fun aboutText(): String{
        return "Developed by Vu Duc Trung"+
                "If you want to provide feedback, please contact me: Facebook.com/2k.trung"+
                "Welcome and give you all of love!"
    }
}