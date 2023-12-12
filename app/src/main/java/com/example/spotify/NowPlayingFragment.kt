package com.example.spotify

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.spotify.PlaysongsActivity.Companion.isPlay
import com.example.spotify.PlaysongsActivity.Companion.songPosition
import com.example.spotify.data.setSongPosition
import com.example.spotify.databinding.FragmentNowPlayingBinding


class NowPlayingFragment : Fragment() {
    companion object {
        lateinit var binding: FragmentNowPlayingBinding
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_now_playing, container, false)

        binding = FragmentNowPlayingBinding.bind(view)
        binding.root.visibility = View.INVISIBLE

        binding.btnFrgPausePlayNowPlaying.setOnClickListener {
            if (isPlay) {
                pauseMusicFrg()
            } else {
                playMusicFrg()
            }
        }

        binding.btnFrgNextSongNowPlaying.setOnClickListener{
            setSongPosition(true)
            PlaysongsActivity.musicService!!.createMediaPlayer()
            Glide.with(this).load(PlaysongsActivity.musicList[PlaysongsActivity.songPosition].artUrl)
                .apply(RequestOptions().placeholder(R.drawable.splash_screen).centerCrop())
                .into(binding.imgFrgNowPlaying)
            PlaysongsActivity.binding.txtSongName.text = PlaysongsActivity.musicList[PlaysongsActivity.songPosition].title
            PlaysongsActivity.binding.tvSongNamePlayer.text = PlaysongsActivity.musicList[PlaysongsActivity.songPosition].album
            PlaysongsActivity.musicService!!.showNotification(R.drawable.ic_pause)
            binding.tvSongnameFrgNowPlaying.text = PlaysongsActivity.musicList[songPosition].title
            playMusicFrg()
        }
        binding.root.setOnClickListener {

            val nowPlayingIntent = Intent(requireContext(),PlaysongsActivity::class.java)
            nowPlayingIntent.putExtra("index", PlaysongsActivity.songPosition)
            nowPlayingIntent.putExtra("class","NowPlayingFragment")
            ContextCompat.startActivity(requireContext(),nowPlayingIntent,null)
        }
        return view
    }

    private fun pauseMusicFrg() {
        PlaysongsActivity.musicService!!.mediaPlayer!!.pause()
        PlaysongsActivity.musicService!!.showNotification(R.drawable.ic_play_arrow)
        PlaysongsActivity.binding.btnPausePlaySong.setIconResource(R.drawable.ic_play_arrow)
        isPlay = false
        binding.btnFrgPausePlayNowPlaying.setIconResource(R.drawable.ic_play_arrow)

    }

    private fun playMusicFrg() {
        PlaysongsActivity.musicService!!.mediaPlayer!!.start()
        PlaysongsActivity.musicService!!.showNotification(R.drawable.ic_pause)
        PlaysongsActivity.binding.btnPausePlaySong.setIconResource(R.drawable.ic_pause)
        isPlay = true
        binding.btnFrgPausePlayNowPlaying.setIconResource(R.drawable.ic_pause)
    }

    override fun onResume() {
        super.onResume()
        if (PlaysongsActivity.musicService != null) {
            binding.root.visibility = View.VISIBLE
            binding.tvSongnameFrgNowPlaying.isSelected = true
            Glide.with(this)
                .load(PlaysongsActivity.musicList[PlaysongsActivity.songPosition].artUrl)
                .apply(RequestOptions().placeholder(R.drawable.splash_screen).centerCrop())
                .into(binding.imgFrgNowPlaying)
            binding.tvSongnameFrgNowPlaying.text = PlaysongsActivity.musicList[songPosition].title
            if (isPlay) {
                binding.btnFrgPausePlayNowPlaying.setIconResource(R.drawable.ic_pause)
            } else {
                binding.btnFrgPausePlayNowPlaying.setIconResource(R.drawable.ic_play_arrow)
            }
            PlaysongsActivity.binding.sbSeekBar.progress = PlaysongsActivity.musicService!!.mediaPlayer!!.currentPosition
            PlaysongsActivity.binding.sbSeekBar.max = PlaysongsActivity.musicService!!.mediaPlayer!!.duration
        }
        else{
            binding.root.visibility = View.INVISIBLE
        }

    }

}