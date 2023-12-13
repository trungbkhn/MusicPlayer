package com.example.spotify

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spotify.adapter.FavouriteAdapter
import com.example.spotify.data.Music
import com.example.spotify.data.checkPlaylist
import com.example.spotify.databinding.ActivityFavouriteBinding

private lateinit var binding: ActivityFavouriteBinding
private lateinit var adapter: FavouriteAdapter


class FavouriteActivity : AppCompatActivity() {
    companion object {
        var musicListFavourite = ArrayList<Music>()
        var favouritesChanged: Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityFavouriteBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Spotify)
        setContentView(binding.root)
        getSupportActionBar()?.hide()

        musicListFavourite = checkPlaylist(musicListFavourite)

        adapter = FavouriteAdapter(musicListFavourite, this)

        binding.btnBackFavourite.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


        binding.rcvFavouriteSong.setHasFixedSize(true)
        binding.rcvFavouriteSong.setItemViewCacheSize(13)
        binding.rcvFavouriteSong.layoutManager =
            GridLayoutManager(this, 3, RecyclerView.VERTICAL, false)
        adapter = FavouriteAdapter(musicListFavourite, this)
        binding.rcvFavouriteSong.adapter = adapter
        favouritesChanged = false
        if (musicListFavourite.size < 1) {
            binding.btnShuffleFavourite.visibility = View.INVISIBLE
        }
        if (musicListFavourite.isNotEmpty()) binding.instructionFV.visibility = View.GONE
        binding.btnShuffleFavourite.setOnClickListener {
            val intent = Intent(this, PlaysongsActivity::class.java)
            intent.putExtra("index", 1)
            intent.putExtra("class", "FavouriteShuffle")
            startActivity(intent)
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        if (favouritesChanged) {
            adapter.updateFavourites(musicListFavourite)
            favouritesChanged = false
        }
    }
}