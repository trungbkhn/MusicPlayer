package com.example.spotify

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spotify.adapter.FavouriteAdapter
import com.example.spotify.data.Music
import com.example.spotify.databinding.ActivityFavouriteBinding

private lateinit var binding: ActivityFavouriteBinding
private lateinit var favouriteRecycleView: FavouriteAdapter


class FavouriteActivity : AppCompatActivity() {
    companion object{
        var musicListFavourite = ArrayList<Music>()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityFavouriteBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Spotify)
        setContentView(binding.root)

        binding.btnBackFavourite.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        binding.rcvFavouriteSong.setHasFixedSize(true)
        binding.rcvFavouriteSong.setItemViewCacheSize(13)
        binding.rcvFavouriteSong.layoutManager =
            GridLayoutManager(this, 4, RecyclerView.VERTICAL, false)
        favouriteRecycleView = FavouriteAdapter(musicListFavourite,this)
        binding.rcvFavouriteSong.adapter = favouriteRecycleView


    }
}