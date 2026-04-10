package com.diego.playlistmaker.media.domain.use_case

import android.net.Uri
import com.diego.playlistmaker.media.data.image_storage.ImageStorageRepository

class ImageStorageInteractorImpl(
    private val repository: ImageStorageRepository
): ImageStorageInteractor {
    override fun saveImage(uri: Uri, name: String): String {
        return repository.saveImage(uri, name)
    }

    override fun getImage(name: String): Uri {
        return repository.getImage(name)
    }

    override fun deleteAllImages() {
        repository.deleteAllImages()
    }

    override fun deleteImage(imagePath: String) {
        return repository.deleteImage(imagePath)
    }

    override fun getAllImages(): List<Uri> {
        return repository.getAllImages()
    }
}

interface ImageStorageInteractor{
    fun saveImage(uri: Uri, name: String): String
    fun getImage(name: String): Uri
    fun deleteAllImages()
    fun deleteImage(imagePath: String)
    fun getAllImages(): List<Uri>
}