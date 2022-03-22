package com.lizzaraga.ziker.domains.models

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever

data class SongModel(val id: Long,
                     val albumId: Long,
                     val album: String,
                     val dataPath: String,
                     val artistName: String,
                     val title: String,
                     val size : Long,
                     val duration: Long
){

    val fileName: String get() {
        return dataPath.split('/').last()
    }

    private var _imageCover: Bitmap? = null
    private var _imageCoverIsComputed = false

    val imageCover: Bitmap? get() {
        return if(_imageCoverIsComputed) _imageCover
        else{
            _imageCoverIsComputed = true
            when(val byteData = getAlbumCoverByteData()){
                null -> null
                else -> {
                    _imageCover = BitmapFactory.decodeByteArray(byteData, 0, byteData.size)
                    _imageCover
                }
            }
        }
    }

    private fun getAlbumCoverByteData(): ByteArray?{
        val retriever : MediaMetadataRetriever = MediaMetadataRetriever()
        retriever.setDataSource(dataPath)
        val byteData = retriever.embeddedPicture
        retriever.release()
        return byteData
    }


}
