package com.example.spotify

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spotify.adapter.MusicWorldRecycview
import com.example.spotify.data.Music
import com.example.spotify.databinding.ActivitySelectionBinding

private lateinit var binding: ActivitySelectionBinding
private lateinit var adapter: MusicWorldRecycview

class SelectionActivity : AppCompatActivity() {
    companion object{
        var isSearchSelection = false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTheme(R.style.Theme_Spotify)
        getSupportActionBar()?.hide()
        binding.rcvSearchSong.setHasFixedSize(true)
        binding.rcvSearchSong.setItemViewCacheSize(13)
        binding.rcvSearchSong.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        adapter = MusicWorldRecycview(
            this,
            MainActivity.MusicListMA, listSearch = true
        )
        binding.rcvSearchSong.adapter = adapter
        binding.svSeachViewDetailPlaylist.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                MainActivity.musicListSearch = ArrayList<Music>()
                if (newText != null) {
                    val userInput = newText.lowercase()
                    for (song in MainActivity.MusicListMA) {
                        if (song.title.lowercase().contains(userInput)) {
                            MainActivity.musicListSearch.addAll(listOf(song))
                        } else {

                        }
                        isSearchSelection = true
                        adapter.updateMusicList(MainActivity.musicListSearch)
                        adapter.notifyDataSetChanged()
                    }
                }
                return true
            }
        })
    }
}