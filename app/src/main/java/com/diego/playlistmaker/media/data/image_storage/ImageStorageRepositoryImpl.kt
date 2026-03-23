package com.diego.playlistmaker.media.data.image_storage

import android.net.Uri

interface ImageStorageRepository {
    fun saveImage(uri: Uri, name: String): String
    fun getImage(name: String): Uri
    fun deleteAllImages()

    fun getAllImages(): List<Uri>
}