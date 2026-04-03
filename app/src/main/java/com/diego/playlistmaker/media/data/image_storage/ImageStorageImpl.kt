package com.diego.playlistmaker.media.data.image_storage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.core.net.toUri
import java.io.File
import java.io.FileOutputStream

class ImageStorageImpl(
    private val context: Context
) : ImageStorageRepository {

    override fun saveImage(uri: Uri, name: String): String {
        val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), PACKAGE_NAME)
        if (!dir.exists()) dir.mkdirs()

        val file = File(dir, "${System.currentTimeMillis()}.jpg")
        if (file.exists()) file.delete()

        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { output ->
                BitmapFactory.decodeStream(input)?.compress(Bitmap.CompressFormat.JPEG, 30, output)
            }
        }

        return file.absolutePath
    }

    override fun getImage(name: String): Uri {
        return File(
            File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), PACKAGE_NAME),
            "$name.jpg"
        ).toUri()
    }

    override fun getAllImages(): List<Uri> {
        return File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            PACKAGE_NAME
        ).listFiles()?.map { it.toUri() } ?: emptyList()
    }

    override fun deleteAllImages() {
        File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            PACKAGE_NAME
        ).listFiles()?.forEach { if (it.isFile) it.delete() }
    }

    override fun deleteImage(imagePath: String) {
        File(imagePath).delete()
    }

    companion object {
        const val PACKAGE_NAME = "myalbum"
    }
}