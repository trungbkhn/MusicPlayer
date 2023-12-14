package com.example.spotify.data

import android.media.MediaMetadataRetriever
import android.util.Log
import com.example.spotify.FavouriteActivity
import com.example.spotify.PlaysongsActivity
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

fun exitApp(){
    Log.d("ExitApp", "ExitApp function called")
    if(PlaysongsActivity.musicService != null){
        PlaysongsActivity.musicService!!.stopForeground(true)
        PlaysongsActivity.musicService!!.mediaPlayer!!.release()
        PlaysongsActivity.musicService = null
        }
    exitProcess(1)
}
//fun setDialogBtnBackground(context: Context, dialog: AlertDialog){
//    //setting button text
//    dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)?.setTextColor(
//        MaterialColors.getColor(context, R.attr.dialog, Color.WHITE)
//    )
//    dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE)?.setTextColor(
//        MaterialColors.getColor(context, R.attr.dialogTextColor, Color.WHITE)
//    )
//
//    //setting button background
//    dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)?.setBackgroundColor(
//        MaterialColors.getColor(context, R.attr.dialogBtnBackground, Color.RED)
//    )
//    dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE)?.setBackgroundColor(
//        MaterialColors.getColor(context, R.attr.dialogBtnBackground, Color.RED)
//    )
//}



