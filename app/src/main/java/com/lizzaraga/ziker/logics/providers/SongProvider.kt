package com.lizzaraga.ziker.logics.providers

import android.content.Context
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import com.lizzaraga.ziker.domains.models.SongModel

class SongProvider {
    companion object {
        fun getSongs(context: Context) : List<SongModel>{
            val songList = mutableListOf<SongModel>()
            val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val projections = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.DURATION,

            )
            val selection = "${MediaStore.Files.FileColumns.MIME_TYPE}=?"
            val selectionArgs = arrayOf(
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("mp3").toString()
            )
            val cursor = context.contentResolver.query(
                uri,
                projections,
                selection,
                selectionArgs,
                null
            )
             while (cursor!!.moveToNext()){
                songList.add(
                    SongModel(
                        cursor.getLong(0),
                        cursor.getLong(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getLong(6),
                        cursor.getLong(7),
                    )
                )

             }
            Log.d(this::class.java.toString(), "Song list: $songList")
            //Log.d(this::class.java.toString(), "Song Embedded Image List: ${songList.map { it.imageCover }}")
            cursor.close()
            return songList
        }
    }
}

