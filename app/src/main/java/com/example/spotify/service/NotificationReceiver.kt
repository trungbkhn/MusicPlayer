package com.example.spotify.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.spotify.NowPlayingFragment
import com.example.spotify.PlaysongsActivity
import com.example.spotify.R
import com.example.spotify.data.setSongPosition
import kotlin.system.exitProcess

class NotificationReceiver:BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {
        when(p1?.action){
            ApplicationMusic.PLAY -> if(PlaysongsActivity.isPlay) pauseMusic() else playMusic()
            ApplicationMusic.NEXT -> nextSongNotification(true, context = p0!!)
            ApplicationMusic.PREVIOUS -> nextSongNotification(false, context = p0!!)
            ApplicationMusic.EXIT -> {
                PlaysongsActivity.musicService!!.stopForeground(true)
                PlaysongsActivity.musicService = null
                exitProcess(1)
            }

        }
    }

    private fun playMusic(){
        PlaysongsActivity.isPlay = true
        PlaysongsActivity.musicService!!.mediaPlayer!!.start()
        PlaysongsActivity.musicService!!.showNotification(R.drawable.ic_pause)
        PlaysongsActivity.binding.btnPausePlaySong.setIconResource(R.drawable.ic_pause)
        NowPlayingFragment.binding.btnFrgPausePlayNowPlaying.setIconResource(R.drawable.ic_pause)
    }

    private fun pauseMusic(){
        PlaysongsActivity.isPlay = false
        PlaysongsActivity.musicService!!.mediaPlayer!!.pause()
        PlaysongsActivity.musicService!!.showNotification(R.drawable.ic_play_arrow)
        PlaysongsActivity.binding.btnPausePlaySong.setIconResource(R.drawable.ic_play_arrow)
        NowPlayingFragment.binding.btnFrgPausePlayNowPlaying.setIconResource(R.drawable.ic_play_arrow)
    }
    private fun nextSongNotification(increment: Boolean, context: Context){
        setSongPosition(increment)
        PlaysongsActivity.musicService!!.createMediaPlayer()
        Glide.with(context).load(PlaysongsActivity.musicList[PlaysongsActivity.songPosition].artUrl)
            .apply(RequestOptions().placeholder(R.drawable.splash_screen).centerCrop())
            .into(PlaysongsActivity.binding.imgvPlaySongView)
        PlaysongsActivity.binding.txtSongName.text = PlaysongsActivity.musicList[PlaysongsActivity.songPosition].title
        PlaysongsActivity.binding.tvSongNamePlayer.text = PlaysongsActivity.musicList[PlaysongsActivity.songPosition].album
        playMusic()
        if (PlaysongsActivity.isFavor){
            PlaysongsActivity.binding.imgbtnHeart.setImageResource(R.drawable.ic_heart)
        }
        else PlaysongsActivity.binding.imgbtnHeart.setImageResource(R.drawable.ic_heart_empty)
    }



}