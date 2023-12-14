package com.example.spotify

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spotify.adapter.MusicWorldRecycview
import com.example.spotify.data.Music
import com.example.spotify.data.MusicPlaylist
import com.example.spotify.data.exitApp
import com.example.spotify.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File


class MainActivity : AppCompatActivity() {


    companion object {
        lateinit var MusicListMA: ArrayList<Music>
        lateinit var musicListSearch: ArrayList<Music>
        var isSearching = false
        private lateinit var binding: ActivityMainBinding
        private lateinit var toggle: ActionBarDrawerToggle
        private lateinit var musicAdapter: MusicWorldRecycview
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        //set layout
        setNavDraw()


        setContentView(binding.root)
        if (requestRuntimePermission()) {
            initializeLayout()
            storageDataSong()
        }
        //set on click listener
        onClickListener()

    }

    private fun onClickListener() {
        binding.btnFavourite.setOnClickListener {
            val intent = Intent(this@MainActivity, FavouriteActivity::class.java)
            startActivity(intent)
        }

        binding.btnPlaylist.setOnClickListener {
            val intent = Intent(this@MainActivity, PlaylistActivity::class.java)
            startActivity(intent)
        }
        binding.btnShuffle.setOnClickListener {
            val intent = Intent(this@MainActivity, PlaysongsActivity::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "PlaysongsActivity")
            startActivity(intent)
        }
        binding.navMain.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_Feedback -> Toast.makeText(this, "1", Toast.LENGTH_SHORT).show()
                R.id.nav_About -> Toast.makeText(this, "2", Toast.LENGTH_SHORT).show()
                R.id.nav_Setting -> Toast.makeText(this, "3", Toast.LENGTH_SHORT).show()
                R.id.nav_Exit -> {
                    val builder = MaterialAlertDialogBuilder(this)
                    builder.setTitle("Exit").setMessage("Do you want to close app?")
                        .setPositiveButton("Yes") { _, _ ->
                                exitApp()
                        }.setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                    val customDilog = builder.create()
                    customDilog.show()
                    customDilog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
                    customDilog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)

                }
            }
            true
        }
    }

    private fun setNavDraw() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setTheme(R.style.splash_screen)
        // nav drawer

        toggle = ActionBarDrawerToggle(this, binding.root, R.string.open, R.string.close)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


    @RequiresApi(Build.VERSION_CODES.R)
    private fun initializeLayout() {
        MusicListMA = getAllAudio()
        binding.rcvListSongs.setHasFixedSize(true)
        binding.rcvListSongs.setItemViewCacheSize(13)
        binding.rcvListSongs.layoutManager = LinearLayoutManager(this@MainActivity)
        musicAdapter = MusicWorldRecycview(this, MusicListMA)
        binding.rcvListSongs.adapter = musicAdapter
        binding.tvTotalSongs.text = "Total Songs: " + musicAdapter.itemCount
    }


    @SuppressLint("Recycle", "Range")
    private fun getAllAudio(): ArrayList<Music> {
        val tempList = ArrayList<Music>()
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID
        )

        val cursor = this.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            MediaStore.Audio.Media.DATE_ADDED + " DESC",
            null
        )
        if (cursor != null) {
            Log.d("CursorCount", "Number of rows: ${cursor.count}")
            if (cursor.moveToFirst()) {
                do {
                    val titleC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val idC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                    val albumC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    val artistC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val durationC =
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val albumIdc =
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                            .toString()
                    val uri = Uri.parse("content://media/external/audio/albumart")
                    val artUric = Uri.withAppendedPath(uri, albumIdc).toString()
                    val music = Music(
                        id = idC,
                        title = titleC,
                        album = albumC,
                        artist = artistC,
                        path = pathC,
                        duration = durationC,
                        artUrl = artUric
                    )
                    val file = File(music.path)
                    if (file.exists())
                        tempList.add(music)
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
        return tempList
    }

    private fun requestRuntimePermission(): Boolean {

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU){
            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 13)
                return false
            }
        }
        //android 13 permission request
        else if(Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU){
            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_MEDIA_AUDIO), 13)
                return false
            }
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 13) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Pemission granted", Toast.LENGTH_SHORT)
                initializeLayout()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    13
                )
            }
        }
    }

    // sharePreferences
    private fun storageDataSong() {
        FavouriteActivity.musicListFavourite = ArrayList()
        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE)
        val jsonString = editor.getString("FavouriteSongs", null)
        val typeToken = object : TypeToken<ArrayList<Music>>(){}.type
        if(jsonString != null){
            val data: ArrayList<Music> = GsonBuilder().create().fromJson(jsonString, typeToken)
            FavouriteActivity.musicListFavourite.addAll(data)
        }

        PlaylistActivity.musicListPlaylist = MusicPlaylist()
        val jsonStringPlaylist = editor.getString("MusicPlaylist", null)
        if(jsonStringPlaylist != null){
            val dataPlaylist: MusicPlaylist = GsonBuilder().create().fromJson(jsonStringPlaylist, MusicPlaylist::class.java)
            PlaylistActivity.musicListPlaylist = dataPlaylist
        }
        Log.d("Myref", "Da mo")
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.search_view, menu)
        val searchView = menu?.findItem(R.id.sv_seachView)?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                if (p0 != null) {
                    val userInput = p0.lowercase()
                    musicListSearch = ArrayList()
                    for (song in MusicListMA) {
                        if (song.title.lowercase().contains(userInput)) {
                            musicListSearch.add(song)
                        }
                    }
                    isSearching = true
                    musicAdapter.updateMusicList(musicListSearch)
                }
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onDestroy() {
        super.onDestroy()
        if(!PlaysongsActivity.isPlay && PlaysongsActivity.musicService != null){
            exitApp()
        }
        //for storing favourites data using shared preferences
        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()
        val jsonString = GsonBuilder().create().toJson(FavouriteActivity.musicListFavourite)
        editor.putString("FavouriteSongs", jsonString)
        val jsonStringPlaylist = GsonBuilder().create().toJson(PlaylistActivity.musicListPlaylist)
        editor.putString("MusicPlaylist", jsonStringPlaylist)
        editor.apply()
        Log.d("Myref", "Da luu")
    }



    override fun onResume() {
        super.onResume()

        //for storing favourites data using shared preferences
        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()
        val jsonString = GsonBuilder().create().toJson(FavouriteActivity.musicListFavourite)
        editor.putString("FavouriteSongs", jsonString)
        val jsonStringPlaylist = GsonBuilder().create().toJson(PlaylistActivity.musicListPlaylist)
        editor.putString("MusicPlaylist", jsonStringPlaylist)
        editor.apply()
        if(PlaysongsActivity.musicService != null) binding.fgmNowPlaying.visibility = View.VISIBLE
        Log.d("Myref", "Da luu")
    }

    override fun onPause() {
        super.onPause()

        // Store favorites and playlist data in SharedPreferences
        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()
        val jsonString = GsonBuilder().create().toJson(FavouriteActivity.musicListFavourite)
        editor.putString("FavouriteSongs", jsonString)
        val jsonStringPlaylist = GsonBuilder().create().toJson(PlaylistActivity.musicListPlaylist)
        editor.putString("MusicPlaylist", jsonStringPlaylist)
        editor.apply()
        Log.d("Myref", "Da luu")
    }
}