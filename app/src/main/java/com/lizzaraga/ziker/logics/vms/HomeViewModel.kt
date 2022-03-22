package com.lizzaraga.ziker.logics.vms

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lizzaraga.ziker.domains.models.FileDropdownAction
import com.lizzaraga.ziker.domains.models.SongModel
import com.lizzaraga.ziker.logics.providers.SongProvider

class HomeViewModel(application: Application): AndroidViewModel(application) {

    var songList = mutableStateListOf<SongModel>()
        private set
    var isListView by mutableStateOf(true)
        private set

    var selectedSongs = mutableStateListOf<SongModel>()
        private set

    val selectedSongIds : List<Long> get() = selectedSongs.map { it.id }


    val inSelectedMode: Boolean get() = selectedSongIds.isNotEmpty()

    fun onLoadSong(){
        val data = SongProvider.getSongs(getApplication())
        songList.addAll(data)
    }

    fun onListView(){
        isListView = true
    }
    fun onGridView(){
        isListView = false
    }

    fun onSelectedSong(songModel: SongModel){
        selectedSongs.add(songModel)
        Log.d(this::class.java.toString(), "Selected Song Ids : ${selectedSongIds.map { it }}")
    }
    fun onUnSelectedSong(songModel: SongModel){
        selectedSongs.remove(songModel)
    }
    fun onResetSelection(){
        selectedSongs.clear()
    }

    /**
     * Fire when we are in Selection Mode
     */
    fun onSelectedFileAction(action: FileDropdownAction){

        when(action){
            FileDropdownAction.SelectAll -> selectedSongs = selectedSongs.apply {
                addAll(songList.filterNot { selectedSongs.contains(it) })
            }
            FileDropdownAction.DeselectAll -> selectedSongs.clear()
            else -> {

            }
        }
    }
    /**
     * Fire when we try to make more actions on a song directly
     */
    fun onSelectedFileAction(action: FileDropdownAction, songModel: SongModel){
        when(action){
            FileDropdownAction.Select -> selectedSongs.add(songModel)
            else -> {

            }
        }
    }
}