package com.example.spotify

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.spotify.MainActivity
import com.example.spotify.databinding.ActivityPlaylistBinding

private lateinit var binding: ActivityPlaylistBinding
class PlaylistActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityPlaylistBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Spotify)
        setContentView(binding.root)





        binding.btnBackPlaylist.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}