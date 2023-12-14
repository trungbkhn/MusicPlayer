package com.example.spotify

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.spotify.adapter.MusicWorldRecycview
import com.example.spotify.databinding.ActivityPlaylistDetailsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder

class PlaylistDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlaylistDetailsBinding
    private lateinit var adapter: MusicWorldRecycview

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
        adapter = MusicWorldRecycview(
            this,
            PlaylistActivity.musicListPlaylist.ref[currentPlaylistPos].playList,
            true
        )
        binding.rcvPlaylistDetailSong.adapter = adapter
        binding.btnBackPlaylistDetail.setOnClickListener {
            finish()
        }
        binding.btnAddPlaylist.setOnClickListener {
            val intent = Intent(this, SelectionActivity::class.java)
            startActivity(intent)
        }
        binding.btnRemovePlaylist.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("Remove")
                .setMessage("Do you want to remove all songs from playlist?")
                .setPositiveButton("Yes") { dialog, _ ->
                    PlaylistActivity.musicListPlaylist.ref[currentPlaylistPos].playList.clear()
                    adapter.refreshPlaylist()
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
            val customDialog = builder.create()
            customDialog.show()

//            setDialogBtnBackground(this, customDialog)
        }
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
        adapter.notifyDataSetChanged()

        //for storing favourites data using shared preferences
        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()
        val jsonStringPlaylist = GsonBuilder().create().toJson(PlaylistActivity.musicListPlaylist)
        editor.putString("MusicPlaylist", jsonStringPlaylist)
        editor.apply()

    }
}


