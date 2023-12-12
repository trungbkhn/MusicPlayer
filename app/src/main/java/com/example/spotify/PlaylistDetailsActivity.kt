package com.example.spotify

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.spotify.adapter.MusicWorldRecycview
import com.example.spotify.databinding.ActivityPlaylistDetailsBinding

class PlaylistDetailsActivity : AppCompatActivity() {
    lateinit var binding: ActivityPlaylistDetailsBinding
    lateinit var adapter: MusicWorldRecycview

    companion object {
        var currentPlaylistPos = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Spotify)
        binding = ActivityPlaylistDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getSupportActionBar()?.hide()
        currentPlaylistPos = intent.extras?.getInt("PlaylistDetailsActivity") as Int
        binding.rcvPlaylistDetailSong.setItemViewCacheSize(13)
        binding.rcvPlaylistDetailSong.setHasFixedSize(true)
        binding.rcvPlaylistDetailSong.layoutManager = LinearLayoutManager(this)
        PlaylistActivity.musicListPlaylist.ref[currentPlaylistPos].playList.addAll(MainActivity.MusicListMA)
        adapter = MusicWorldRecycview(
            this,
            PlaylistActivity.musicListPlaylist.ref[currentPlaylistPos].playList,
            true
        )
        binding.rcvPlaylistDetailSong.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        binding.txtPlaylistName.text =
            PlaylistActivity.musicListPlaylist.ref[currentPlaylistPos].name
        if (adapter.itemCount == 1) {
            binding.tvPlaylistDetailName.text =
                "Total ${adapter.itemCount} song.\n\n Create on: ${PlaylistActivity.musicListPlaylist.ref[currentPlaylistPos].createdOn}\n" +
                        "\n" + " --${PlaylistActivity.musicListPlaylist.ref[currentPlaylistPos].createdBy}"
        } else {
            binding.tvPlaylistDetailName.text =
                "Total ${adapter.itemCount} songs.\n\n Create on: ${PlaylistActivity.musicListPlaylist.ref[currentPlaylistPos].createdOn}\n" +
                        "\n" + " --${PlaylistActivity.musicListPlaylist.ref[currentPlaylistPos].createdBy}"
        }
        if (adapter.itemCount > 0){
            Glide.with(this).load(PlaylistActivity.musicListPlaylist.ref[currentPlaylistPos].playList[0].artUrl).apply {
                RequestOptions().placeholder(R.drawable.splash_screen)
            }.into(binding.imgvDetailPlaylistSong)
            binding.rcvPlaylistDetailSong.visibility = View.VISIBLE
        }

    }
}