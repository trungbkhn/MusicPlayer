package com.example.spotify.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.spotify.MainActivity
import com.example.spotify.PlaylistActivity
import com.example.spotify.PlaylistDetailsActivity
import com.example.spotify.PlaysongsActivity
import com.example.spotify.R
import com.example.spotify.SelectionActivity
import com.example.spotify.data.Music
import com.example.spotify.data.formatDuration
import com.example.spotify.databinding.LayoutSongRcvBinding

class MusicWorldRecycview(
    private val context: Context,
    private var musicList: ArrayList<Music>,
    private val playlistDetail: Boolean = false,
    private val listSearch: Boolean = false
) :
    RecyclerView.Adapter<MusicWorldViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicWorldViewHolder {
        return MusicWorldViewHolder(
            LayoutSongRcvBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    override fun onBindViewHolder(holder: MusicWorldViewHolder, position: Int) {
        holder.title.text = musicList[position].title
        holder.album.text = musicList[position].album
        holder.duration.text = formatDuration(musicList[position].duration)
        Glide.with(context).load(musicList[position].artUrl)
            .apply(RequestOptions().placeholder(R.drawable.splash_screen).centerCrop())
            .into(holder.image)
        when {
            playlistDetail -> holder.root.setOnClickListener {
                sendIntent("PlaylistDetailsAdapter", position)
            }

            listSearch -> holder.root.setOnClickListener {
                if (addSong(musicList[position])) {
                    holder.root.setBackgroundColor(ContextCompat.getColor(context, R.color.pink))
                } else {
                    holder.root.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
                }
                when{
                SelectionActivity.isSearchSelection -> sendIntent("MusicAdapterSearch", position)
                musicList[position].id == PlaysongsActivity.musicListId -> sendIntent(
                "NowPlayingFragment",
                PlaysongsActivity.songPosition
                )}
            }

            else -> {
                holder.root.setOnClickListener {
                    when {
                        MainActivity.isSearching -> sendIntent("MusicAdapterSearch", position)
                        musicList[position].id == PlaysongsActivity.musicListId -> sendIntent(
                            "NowPlayingFragment",
                            PlaysongsActivity.songPosition
                        )


                        else -> sendIntent("MusicWorldRecycview", position)
                    }

                }
            }
        }
    }


    fun sendIntent(ref: String, pos: Int) {
        val intent = Intent(context, PlaysongsActivity::class.java)
        intent.putExtra("index", pos)
        intent.putExtra("class", ref)
        ContextCompat.startActivity(context, intent, null)
    }

    fun updateMusicList(searchList: ArrayList<Music>) {
        musicList = ArrayList()
        musicList.addAll(searchList)
        notifyDataSetChanged()
    }

    fun addSong(song: Music): Boolean {
        PlaylistActivity.musicListPlaylist.ref[PlaylistDetailsActivity.currentPlaylistPos].playList.forEachIndexed { index, music ->
            if (song.id == music.id) {
                PlaylistActivity.musicListPlaylist.ref[PlaylistDetailsActivity.currentPlaylistPos].playList.removeAt(
                    index
                )
                return false
            }

        }
        PlaylistActivity.musicListPlaylist.ref[PlaylistDetailsActivity.currentPlaylistPos].playList.add(
            song)
        return true
    }
    fun refreshPlaylist(){
        musicList = ArrayList()
        musicList = PlaylistActivity.musicListPlaylist.ref[PlaylistDetailsActivity.currentPlaylistPos].playList
        notifyDataSetChanged()
    }



}
