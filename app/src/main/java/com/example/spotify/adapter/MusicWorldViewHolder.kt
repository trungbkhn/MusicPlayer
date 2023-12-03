package com.example.spotify.adapter

import androidx.recyclerview.widget.RecyclerView
import com.example.spotify.databinding.LayoutSongRcvBinding

class MusicWorldViewHolder(binding: LayoutSongRcvBinding): RecyclerView.ViewHolder(binding.root) {
    val title = binding.txtSongNameRcv
    val album = binding.txtSongAlbum
    val image = binding.imgvSongView
    val duration = binding.tvSongDuration
    val root = binding.root
}