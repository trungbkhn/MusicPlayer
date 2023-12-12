package com.example.spotify.data

import android.content.Context
import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.spotify.FavouriteActivity
import com.example.spotify.MainActivity
import com.example.spotify.PlaylistActivity
import com.example.spotify.PlaysongsActivity
import com.google.gson.GsonBuilder
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

data class Music(
    val id: String,
    val album: String,
    val title: String,
    val artist: String,
    val duration: Long = 0,
    val path: String,
    val artUrl: String
)
class Playlist(){
    lateinit var name: String
    lateinit var playList: ArrayList<Music>
    lateinit var createdBy: String
    lateinit var createdOn: String
}

class MusicPlaylist(){
    var ref: ArrayList<Playlist> = ArrayList()
}

fun formatDuration(duration: Long): String {
    val minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
    val seconds = (TimeUnit.SECONDS.convert(
        duration,
        TimeUnit.MILLISECONDS
    ) - minutes * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES))
    return String.format("%02d:%02d", minutes, seconds)
}

fun getImgArt(path: String): ByteArray? {
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(path)
    return retriever.embeddedPicture
}

fun setSongPosition(increment: Boolean) {
    if (!PlaysongsActivity.isRepeating){
        if (increment) {
            if (PlaysongsActivity.musicList.size - 1 == PlaysongsActivity.songPosition) {
                PlaysongsActivity.songPosition = 0
            } else {
                ++PlaysongsActivity.songPosition
            }

        } else {
            if (PlaysongsActivity.songPosition == 0) {
                PlaysongsActivity.songPosition = PlaysongsActivity.musicList.size - 1
            } else {
                --PlaysongsActivity.songPosition
            }
        }
    }

}

fun checkFavorIndex(id: String): Int{
    PlaysongsActivity.isFavor = false
    FavouriteActivity.musicListFavourite.forEachIndexed{index, music ->
        if (music.id.contains(id)){
            PlaysongsActivity.isFavor = true
            return index
        }
    }
    return -1
}

fun checkPlaylist(playlist: ArrayList<Music>): ArrayList<Music>{
    playlist.forEachIndexed { index, music ->
        val file = File(music.path)
        if(!file.exists())
            playlist.removeAt(index)
    }
    return playlist
}

fun saveFavoriteList(context: Context) {
    val editor = context.getSharedPreferences(MainActivity.PREF_NAME, AppCompatActivity.MODE_PRIVATE).edit()
    val jsonString = GsonBuilder().create().toJson(FavouriteActivity.musicListFavourite)
    editor.putString("MusicListFavourite", jsonString)
    val jsonStringPlaylist = GsonBuilder().create().toJson(PlaylistActivity.musicListPlaylist)
    editor.putString("MusicPlaylist", jsonStringPlaylist)
    editor.apply()

}
fun exitApp(){
    Log.d("ExitApp", "ExitApp function called")
    if(PlaysongsActivity.musicService != null){
        PlaysongsActivity.musicService!!.stopForeground(true)
        PlaysongsActivity.musicService!!.mediaPlayer!!.release()
        PlaysongsActivity.musicService = null}
    exitProcess(1)

}



