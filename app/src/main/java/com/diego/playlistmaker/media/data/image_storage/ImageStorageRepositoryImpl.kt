package com.diego.playlistmaker.media.data.image_storage

import android.net.Uri


class ImageStorageRepositoryImpl(
    private val storage: ImageStorage
) : ImageStorageRepository {
    override fun saveImage(uri: Uri, name: String): String {
        return storage.saveImage(uri, name)
    }

    override fun getImage(name: String): Uri {
        return storage.getImage(name)
    }

    override fun deleteAllImages() {
        storage.deleteAllImages()
    }

    override fun getAllImages(): List<Uri> {
        return storage.getAllImages()
    }
}

interface ImageStorageRepository {
    fun saveImage(uri: Uri, name: String): String
    fun getImage(name: String): Uri
    fun deleteAllImages()

    fun getAllImages(): List<Uri>
}