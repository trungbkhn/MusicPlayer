package com.example.spotify

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spotify.MainActivity
import com.example.spotify.adapter.PlaylistAdapter
import com.example.spotify.data.MusicPlaylist
import com.example.spotify.data.Playlist
import com.example.spotify.databinding.ActivityPlaylistBinding
import com.example.spotify.databinding.LayoutAddsongdialogcustomviewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private lateinit var binding: ActivityPlaylistBinding
private lateinit var playlistRecycleView: PlaylistAdapter
private lateinit var adapter: PlaylistAdapter

class PlaylistActivity : AppCompatActivity() {

    companion object {
        var musicListPlaylist = MusicPlaylist()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityPlaylistBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Spotify)
        setContentView(binding.root)
        getSupportActionBar()?.hide()


        binding.btnBackPlaylist.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        binding.rcvPlaylistSong.setHasFixedSize(true)
        binding.rcvPlaylistSong.setItemViewCacheSize(13)
        binding.rcvPlaylistSong.layoutManager =
            GridLayoutManager(this, 2, RecyclerView.VERTICAL, false)
        adapter = PlaylistAdapter(musicListPlaylist.ref, this)
        binding.rcvPlaylistSong.adapter = adapter
        binding.btnAddSong.setOnClickListener {
            customDialog()
        }
    }

    private fun customDialog() {
        val customDiaLog = LayoutInflater.from(this@PlaylistActivity)
            .inflate(R.layout.layout_addsongdialogcustomview, binding.root, false)
        val buider = MaterialAlertDialogBuilder(this)
        val binder = LayoutAddsongdialogcustomviewBinding.bind(customDiaLog)
        buider.setView(customDiaLog).setTitle("Playlist Details")
            .setPositiveButton("ADD") { dialog, _ ->
                val playlistName = binder.tvPlaylistName.text
                val createdBy = binder.tvUserNamePlaylist.text
                if (playlistName != null && createdBy != null) {
                    if (playlistName.isNotEmpty() && createdBy.isNotEmpty()) {
                        addPlaylist(playlistName.toString(), createdBy.toString())
                    }

                }
                dialog.dismiss()

            }.show()
    }

    private fun addPlaylist(playlistName: String, createdBy: String) {
        var playListExists = false
        for (i in musicListPlaylist.ref) {
            if (playlistName.equals(i.name)) {
                playListExists = true
                break
            }
        }
        if (playListExists) {
            Toast.makeText(this, "This name of your Playlist existed !", Toast.LENGTH_SHORT).show()
        } else {
            val tempPlaylist = Playlist()
            tempPlaylist.name = playlistName
            tempPlaylist.playList = ArrayList()
            tempPlaylist.createdBy = createdBy
            val caledar = Calendar.getInstance().time
            val sdf = SimpleDateFormat("dd MM yyyy", Locale.ENGLISH)
            tempPlaylist.createdOn = sdf.format(caledar)
            musicListPlaylist.ref.add(tempPlaylist)
            adapter.refreshPlaylist()
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }
}