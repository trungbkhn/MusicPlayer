package com.example.spotify.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.spotify.PlaysongsActivity
import com.example.spotify.R
import com.example.spotify.data.Music
import com.example.spotify.databinding.LayoutFavouriteViewCustomBinding

class FavouriteAdapter(private var musicList: ArrayList<Music>, private val context: Context) :
    RecyclerView.Adapter<FavouriteAdapter.FavouriteViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        return FavouriteViewHolder(
            LayoutFavouriteViewCustomBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        holder.songName.text = musicList[position].title
        Glide.with(context).load(musicList[position].artUrl)
            .apply(RequestOptions().placeholder(R.drawable.splash_screen).centerCrop())
            .into(holder.image)
        
        // Xử lý sự kiện click
        holder.root.setOnClickListener {
                val intent = Intent(context, PlaysongsActivity::class.java)
                intent.putExtra("index", position)
                intent.putExtra("class", "FavouriteAdapter")
                ContextCompat.startActivity(context, intent, null)
            }
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    class FavouriteViewHolder(binding: LayoutFavouriteViewCustomBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val image = binding.imgSongFavourite
        val songName = binding.tvSongNameFavourite
        val root = binding.root
    }

    fun sendIntent(ref: String, pos: Int) {
        val intent = Intent(context, PlaysongsActivity::class.java)
        intent.putExtra("index", pos)
        intent.putExtra("class", ref)
        ContextCompat.startActivity(context, intent, null)
    }

    fun updateFavourites(newMusicList: ArrayList<Music>) {
        musicList = ArrayList()
        musicList.addAll(newMusicList)
        notifyDataSetChanged()
    }


}