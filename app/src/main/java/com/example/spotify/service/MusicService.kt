package com.example.spotify.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.example.spotify.MainActivity
import com.example.spotify.PlaysongsActivity
import com.example.spotify.R
import com.example.spotify.data.formatDuration
import com.example.spotify.data.getImgArt

class MusicService : Service() {
    private val myBinder = MyBinder()
    var mediaPlayer: MediaPlayer? = null
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var runnable: Runnable

    override fun onBind(p0: Intent?): IBinder? {
        mediaSession = MediaSessionCompat(baseContext, "My music")
        return myBinder
    }

    inner class MyBinder : Binder() {
        fun currentService(): MusicService {
            return this@MusicService
        }
    }


    fun showNotification(btn_PlayPause: Int) {

        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val intentPlaySong = Intent(baseContext, PlaysongsActivity::class.java)
        intentPlaySong.putExtra("index", PlaysongsActivity.songPosition)
        intentPlaySong.putExtra("class", "NowPlayingFragment")
        val contentIntent = PendingIntent.getActivity(
            this, 0, intentPlaySong, flag
        )
        val intent = Intent(baseContext, MainActivity::class.java)



        val prevIntent = Intent(
            baseContext,
            NotificationReceiver::class.java
        ).setAction(ApplicationMusic.PREVIOUS)
        val prevPendingIntent = PendingIntent.getBroadcast(baseContext, 0, prevIntent, flag)

        val playIntent =
            Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationMusic.PLAY)
        val playPendingIntent = PendingIntent.getBroadcast(baseContext, 0, playIntent, flag)

        val nextIntent =
            Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationMusic.NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(baseContext, 0, nextIntent, flag)

        val exitIntent =
            Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationMusic.EXIT)
        val exitPendingIntent = PendingIntent.getBroadcast(baseContext, 0, exitIntent, flag)


        val imgArt = getImgArt(PlaysongsActivity.musicList[PlaysongsActivity.songPosition].path)
        val image = if (imgArt == null) {
            BitmapFactory.decodeResource(resources, R.drawable.splash_screen)
        } else {
            BitmapFactory.decodeByteArray(imgArt, 0, imgArt.size)
        }

        val notification =
            NotificationCompat.Builder(baseContext, ApplicationMusic.CHANNEL_ID)
                .setContentIntent(contentIntent)
                .setContentTitle(PlaysongsActivity.musicList[PlaysongsActivity.songPosition].title)
                .setContentText(PlaysongsActivity.musicList[PlaysongsActivity.songPosition].artist)
                .setSmallIcon(R.drawable.ic_music)
                .setLargeIcon(image)
                .setStyle(
                    androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.sessionToken)
                )
                .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
                .setVisibility(androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC)
                .setOnlyAlertOnce(true)
                .addAction(R.drawable.ic_back, "Previous", prevPendingIntent)
                .addAction(btn_PlayPause, "Play", playPendingIntent)
                .addAction(R.drawable.ic_next, "Next", nextPendingIntent)
                .addAction(R.drawable.ic_exit_to_app, "Exit", exitPendingIntent)
                .build()

        startForeground(13, notification)
    }

    fun createMediaPlayer() {
        try {
            if (PlaysongsActivity.musicService!!.mediaPlayer == null) {
                PlaysongsActivity.musicService!!.mediaPlayer = MediaPlayer()
            }
            PlaysongsActivity.musicService!!.mediaPlayer!!.reset()
            PlaysongsActivity.musicService!!.mediaPlayer!!.setDataSource(PlaysongsActivity.musicList[PlaysongsActivity.songPosition].path)
            PlaysongsActivity.musicService!!.mediaPlayer!!.prepare()
            PlaysongsActivity.binding.btnPausePlaySong.setIconResource(R.drawable.ic_pause)
            PlaysongsActivity.musicService!!.showNotification(R.drawable.ic_pause)
            PlaysongsActivity.binding.tvTimeRunSeekBarStart.text =
                formatDuration(mediaPlayer!!.currentPosition.toLong())
            PlaysongsActivity.binding.tvTimeRunSeekBarEnd.text =
                formatDuration(mediaPlayer!!.duration.toLong())
            PlaysongsActivity.binding.sbSeekBar.progress = 0
            PlaysongsActivity.binding.sbSeekBar.max =
                PlaysongsActivity.musicService!!.mediaPlayer!!.duration
            PlaysongsActivity.musicListId =
                PlaysongsActivity.musicList[PlaysongsActivity.songPosition].id
        } catch (e: Exception) {
            return
        }
    }

    fun seekBarSetup() {
        runnable = Runnable {
            PlaysongsActivity.binding.tvTimeRunSeekBarStart.text =
                formatDuration(mediaPlayer!!.currentPosition.toLong())
            PlaysongsActivity.binding.sbSeekBar.progress = mediaPlayer!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable, 200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable, 0)
    }

}


