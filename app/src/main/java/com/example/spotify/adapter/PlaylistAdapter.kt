package com.example.spotify.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.spotify.PlaylistActivity
import com.example.spotify.PlaylistDetailsActivity
import com.example.spotify.PlaysongsActivity
import com.example.spotify.R
import com.example.spotify.data.Playlist
import com.example.spotify.databinding.LayoutPlaylistViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PlaylistAdapter(
    private var musicPlayListList: ArrayList<Playlist>,
    private val context: Context
) :
    RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {

    class PlaylistViewHolder(binding: LayoutPlaylistViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val image = binding.imgSongPlaylist
        val songName = binding.tvSongNamePlaylist
        val btn_delete = binding.btnDeleteSongPlaylist
        val root = binding.root
        val delete = binding.btnDeleteSongPlaylist
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        return PlaylistViewHolder(
            LayoutPlaylistViewBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.songName.text = musicPlayListList[position].name
        holder.songName.isSelected = true
        holder.delete.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(context)
            builder.setTitle(musicPlayListList[position].name)
                .setMessage("Do you want to delete playlist ?")
                .setPositiveButton("Yes") { dialog, _ ->
                    PlaylistActivity.musicListPlaylist.ref.removeAt(position)
                    refreshPlaylist()
                    dialog.dismiss()
                }.setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            val customDilog = builder.create()
            customDilog.show()
            customDilog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
            customDilog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
        }

//
        holder.root.setOnClickListener {
            val intent = Intent(context, PlaylistDetailsActivity::class.java)
            intent.putExtra("PlaylistDetailActivity", position)
            ContextCompat.startActivity(context, intent, null)
        }
        if (PlaylistActivity.musicListPlaylist.ref[position].playList.size > 0) {
            Glide.with(context)
                .load(PlaylistActivity.musicListPlaylist.ref[PlaylistDetailsActivity.currentPlaylistPos].playList[0].artUrl)
                .apply { RequestOptions().placeholder(R.drawable.splash_screen).centerCrop() }.into(holder.image)
        }
    }

    override fun getItemCount(): Int {
        return musicPlayListList.size
    }


    fun sendIntent(ref: String, pos: Int) {
        val intent = Intent(context, PlaysongsActivity::class.java)
        intent.putExtra("index", pos)
        intent.putExtra("class", ref)
        ContextCompat.startActivity(context, intent, null)
    }

    fun updateData(newMusicListList: ArrayList<Playlist>) {
        musicPlayListList = newMusicListList
        notifyDataSetChanged()
    }

    fun refreshPlaylist() {
        musicPlayListList = ArrayList()
        musicPlayListList.addAll(PlaylistActivity.musicListPlaylist.ref)
        notifyDataSetChanged()
    }

}