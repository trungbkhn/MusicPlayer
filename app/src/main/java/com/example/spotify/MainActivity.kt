package com.example.spotify

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
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
import com.example.spotify.data.exitApp
import com.example.spotify.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity() {


    companion object {
        lateinit var MusicListMA: ArrayList<Music>
        lateinit var musicListSearch: ArrayList<Music>
        var isSearching = false
        private const val PREF_NAME = "MyPrefs"
        private const val KEY_FAV_LIST = "favoriteList"
        private lateinit var binding: ActivityMainBinding
        private lateinit var toggle: ActionBarDrawerToggle
        private lateinit var musicAdapter: MusicWorldRecycview
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        //set layout
        setNavDraw()
        if (requestRuntimePermission()) {
            initializeLayout()

            storageDataFavouriteSong()
        }

        setContentView(binding.root)
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
                            if (PlaysongsActivity.musicService != null) {
                                exitApp()
                            }
                            exitProcess(1)
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

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                13
            )
            return false
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
    private fun storageDataFavouriteSong() {
        //FavouriteActivity.musicListFavourite = ArrayList()
        val sharePreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val favListJson = sharePreferences.getString(KEY_FAV_LIST, null)
        val type = object : TypeToken<ArrayList<Music>>() {}.type

        if (favListJson != null) {
            val data: ArrayList<Music> = GsonBuilder().create().fromJson(favListJson,type)
            FavouriteActivity.musicListFavourite.addAll(data)
        }
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
        if (PlaysongsActivity.isPlay && PlaysongsActivity.musicService != null) {
            exitApp()
        }
    }



    override fun onResume() {
        super.onResume()
        val editor = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
        val favListJson = GsonBuilder().create().toJson(FavouriteActivity.musicListFavourite)
        editor.putString(KEY_FAV_LIST, favListJson)
        editor.apply()

    }
}