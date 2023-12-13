package com.example.spotify

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.spotify.data.Music
import com.example.spotify.data.checkFavorIndex
import com.example.spotify.data.exitApp
import com.example.spotify.data.formatDuration
import com.example.spotify.data.setSongPosition
import com.example.spotify.databinding.ActivityPlaysongsBinding
import com.example.spotify.service.MusicService
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class PlaysongsActivity : AppCompatActivity(), ServiceConnection, MediaPlayer.OnCompletionListener {

    companion object {
        lateinit var musicList: ArrayList<Music>
        var songPosition: Int = 0
        var isPlay = false
        var musicService: MusicService? = null
        lateinit var binding: ActivityPlaysongsBinding
        var isRepeating = false
        var min15 = false
        var min30 = false
        var min60 = false
        var musicListId: String = ""
        var isFavor = false
        private var fIndex = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityPlaysongsBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Spotify)
        setContentView(binding.root)
        getSupportActionBar()?.hide()


        initializeLayout()
        btnClick()

    }

    private fun btnClick() {
        binding.imgbtnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.btnPausePlaySong.setOnClickListener {
            if (isPlay == true) {
                pauseMusic()
            } else {
                playMusic()
            }
        }

        binding.btnBackPlaySong.setOnClickListener {
            nextPlaySong(increment = false)
        }

        binding.btnPlayNextSong.setOnClickListener {
            nextPlaySong(increment = true)
        }
        binding.sbSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (p2) musicService!!.mediaPlayer!!.seekTo(p1)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) = Unit

            override fun onStopTrackingTouch(p0: SeekBar?) = Unit

        })
        binding.btnRepeatSongPlayer.setOnClickListener {
            if (isRepeating) {
                isRepeating = false
                binding.btnRepeatSongPlayer.setColorFilter(
                    ContextCompat.getColor(
                        this,
                        R.color.pink
                    )
                )

            } else {
                isRepeating = true
                binding.btnRepeatSongPlayer.setColorFilter(
                    ContextCompat.getColor(
                        this,
                        R.color.purple
                    )
                )
            }
        }
        binding.btnEqPlaySong.setOnClickListener {
            try {
                val eqIntent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                eqIntent.putExtra(
                    AudioEffect.EXTRA_AUDIO_SESSION,
                    musicService!!.mediaPlayer!!.audioSessionId
                )
                eqIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, baseContext.packageName)
                eqIntent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
                startActivityForResult(eqIntent, 13)
            } catch (e: Exception) {
                Toast.makeText(this, "This Equalizer Feature not supported!", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.btnTimerPlaySong.setOnClickListener {
            var timer = min15 || min30 || min60
            if (!timer) {
                showTimerDialog()
            }
            else {
                val builder = MaterialAlertDialogBuilder(this)
                builder.setTitle("Stop Timer").setMessage("Do you want to stop Timer ?")
                    .setPositiveButton("Yes") { _, _ ->
                        min15 = false
                        min30 = false
                        min60 = false
                        binding.btnTimerPlaySong.setColorFilter(ContextCompat.getColor(this,R.color.red))
                    }
                    .setNegativeButton("No") {dialog,_ -> dialog.dismiss()}
                val customDialog = builder.create()
                customDialog.show()
                customDialog.getButton(BUTTON_POSITIVE).setTextColor(Color.RED)
                customDialog.getButton(BUTTON_NEGATIVE).setTextColor(Color.RED)
            }
        }

        binding.btnSharePlaySong.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "Audio/*"
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(musicList[songPosition].path))
            startActivity(Intent.createChooser(shareIntent,"Sharing Music File"))
        }

        binding.imgbtnHeart.setOnClickListener {
            fIndex = checkFavorIndex(musicList[songPosition].id)
            if (isFavor){
                isFavor = false
                binding.imgbtnHeart.setImageResource(R.drawable.ic_heart_empty)
                FavouriteActivity.musicListFavourite.removeAt(fIndex)
            }
            else{
                isFavor = true
                binding.imgbtnHeart.setImageResource(R.drawable.ic_heart)
                FavouriteActivity.musicListFavourite.add(musicList[songPosition])
            }
            FavouriteActivity.favouritesChanged = true
        }


    }

    @SuppressLint("SuspiciousIndentation")
    private fun createMediaPlayer() {
        try {
            if (musicService!!.mediaPlayer == null) {
                musicService!!.mediaPlayer = MediaPlayer()
            }
            musicService!!.mediaPlayer!!.reset()
            musicService!!.mediaPlayer!!.setDataSource(musicList[songPosition].path)
            musicService!!.mediaPlayer!!.prepare()
            musicService!!.mediaPlayer!!.start()
            isPlay = true
            binding.btnPausePlaySong.setIconResource(R.drawable.ic_pause)
            binding.tvTimeRunSeekBarStart.text =
                formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
            binding.tvTimeRunSeekBarEnd.text =
                formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
            binding.sbSeekBar.progress = 0
            binding.sbSeekBar.max = musicService!!.mediaPlayer!!.duration
            musicService!!.mediaPlayer!!.setOnCompletionListener(this)
            musicService!!.showNotification(R.drawable.ic_pause)
            musicListId = musicList[songPosition].id
            playMusic()
        } catch (e: Exception) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun initializeLayout() {
        songPosition = intent.getIntExtra("index", 0)
        when (intent.getStringExtra("class")) {
            "FavouriteAdapter" ->{
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicList = ArrayList()
                musicList.addAll(FavouriteActivity.musicListFavourite)
                setLayout()
            }
            "NowPlayingFragment"->{
                setLayout()
                //setSeekBar
                binding.tvTimeRunSeekBarStart.text = formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
                binding.tvTimeRunSeekBarEnd.text = formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
                binding.sbSeekBar.progress = musicService!!.mediaPlayer!!.currentPosition
                binding.sbSeekBar.max = musicService!!.mediaPlayer!!.duration

                if(isPlay) {
                    binding.btnPausePlaySong.setIconResource(R.drawable.ic_pause)
                }else binding.btnPausePlaySong.setIconResource(R.drawable.ic_play_arrow)
//
            }
            "MusicAdapterSearch" -> {
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicList = ArrayList()
                musicList.addAll(MainActivity.musicListSearch)
                setLayout()
            }
            "MusicWorldRecycview" -> {
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicList = ArrayList()
                musicList.addAll(MainActivity.MusicListMA)
                setLayout()
            }

            "FavouriteShuffle" -> {
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicList = ArrayList()
                musicList.addAll(FavouriteActivity.musicListFavourite)
                musicList.shuffle()
                setLayout()
            }
            "PlaylistDetailsAdapter" -> {
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicList = ArrayList()
                musicList.addAll(PlaylistActivity.musicListPlaylist.ref[PlaylistDetailsActivity.currentPlaylistPos].playList)
                musicList.shuffle()
                setLayout()
            }
//
        }

    }

    private fun setLayout() {
        fIndex = checkFavorIndex(musicList[songPosition].id)
        Glide.with(this).load(musicList[songPosition].artUrl)
            .apply(RequestOptions().placeholder(R.drawable.splash_screen).centerCrop())
            .into(binding.imgvPlaySongView)
        binding.txtSongName.text = musicList[songPosition].title
        binding.tvSongNamePlayer.text = musicList[songPosition].album

        if (isRepeating) {
            binding.btnRepeatSongPlayer.setColorFilter(ContextCompat.getColor(this, R.color.purple))
        }
        if (min15 || min30 || min60) {
            binding.btnTimerPlaySong.setColorFilter(
                ContextCompat.getColor(
                    baseContext,
                    R.color.purple
                )
            )
        }
        else{
            binding.btnTimerPlaySong.setColorFilter(
                ContextCompat.getColor(
                    baseContext,
                    R.color.red))
        }

        if (isFavor){
            binding.imgbtnHeart.setImageResource(R.drawable.ic_heart)
        }
        else{
            binding.imgbtnHeart.setImageResource(R.drawable.ic_heart_empty)
        }

    }

    private fun playMusic() {
        binding.btnPausePlaySong.setIconResource(R.drawable.ic_pause)
        musicService!!.showNotification(R.drawable.ic_pause)
        isPlay = true
        musicService!!.mediaPlayer!!.start()
    }

    private fun pauseMusic() {
        binding.btnPausePlaySong.setIconResource(R.drawable.ic_play_arrow)
        musicService!!.showNotification(R.drawable.ic_play_arrow)
        isPlay = false
        musicService!!.mediaPlayer!!.pause()
    }

    private fun nextPlaySong(increment: Boolean) {
        if (increment) {
            setSongPosition(increment = true)
            setLayout()
            createMediaPlayer()
        } else {
            setSongPosition(increment = false)
            setLayout()
            createMediaPlayer()
        }
    }
    private fun initServiceAndPlaylist(playlist: ArrayList<Music>, shuffle: Boolean, playNext: Boolean = false){
        val intent = Intent(this, MusicService::class.java)
        bindService(intent, this, BIND_AUTO_CREATE)
        startService(intent)
        musicList = ArrayList()
        musicList.addAll(playlist)
        if(shuffle) musicList.shuffle()
        setLayout()
    }

    private fun showTimerDialog() {
        val dialog = BottomSheetDialog(this@PlaysongsActivity)
        dialog.setContentView(R.layout.layout_timer_playsong)
        dialog.show()
        dialog.findViewById<LinearLayout>(R.id.ln_min15)?.setOnClickListener {
            Toast.makeText(this, "The player will stop for 15 minutes !", Toast.LENGTH_SHORT).show()
            binding.btnTimerPlaySong.setColorFilter(ContextCompat.getColor(this,R.color.purple))
            min15 = true
            Thread {
                Thread.sleep((15*60000).toLong())
                if (min15) exitApp()
            }.start()
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.ln_min30)?.setOnClickListener {
            Toast.makeText(this, "The player will stop for 30 minutes !", Toast.LENGTH_SHORT).show()
            binding.btnTimerPlaySong.setColorFilter(ContextCompat.getColor(this,R.color.purple))
            min30 = true
            Thread {
                Thread.sleep((15*60000).toLong())
                if (min30) exitApp()
            }.start()
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.ln_min60)?.setOnClickListener {
            Toast.makeText(this, "The player will stop for 60 minutes !", Toast.LENGTH_SHORT).show()
            binding.btnTimerPlaySong.setColorFilter(ContextCompat.getColor(this,R.color.purple))
            min60 = true
            Thread {
                Thread.sleep((15*60000).toLong())
                if (min60) exitApp()
            }.start()
            dialog.dismiss()
        }
    }


    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
        val binder = p1 as MusicService.MyBinder
        musicService = binder.currentService()
        createMediaPlayer()
        musicService!!.seekBarSetup()

    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        musicService = null

    }

    override fun onCompletion(p0: MediaPlayer?) {
        setSongPosition(increment = true)
        createMediaPlayer()
        setLayout()

        //for refreshing now playing image & text on song completion
        NowPlayingFragment.binding.tvSongnameFrgNowPlaying.isSelected = true
        Glide.with(applicationContext)
            .load(musicList[songPosition].artUrl)
            .apply(RequestOptions().placeholder(R.drawable.splash_screen).centerCrop())
            .into(NowPlayingFragment.binding.imgFrgNowPlaying)
        NowPlayingFragment.binding.tvSongnameFrgNowPlaying.text = musicList[songPosition].title
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 13 || requestCode == RESULT_OK) {
            return
        }
    }

    override fun onResume() {
        super.onResume()
        if (musicService != null && musicService!!.mediaPlayer != null) {
            binding.sbSeekBar.progress = musicService!!.mediaPlayer!!.currentPosition
            binding.sbSeekBar.max = musicService!!.mediaPlayer!!.duration
        }
        
    }

    override fun onDestroy() {
        super.onDestroy()
        if(musicList[songPosition].id == "Unknown" && !isPlay) exitApp()
    }


}