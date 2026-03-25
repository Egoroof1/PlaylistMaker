package com.diego.playlistmaker.media.data.image_storage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.net.toUri
import com.diego.playlistmaker.media.domain.mapper.toLatin
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception


class ImageStorageImpl(
    private val context: Context
): ImageStorageRepository {

    override fun saveImage(uri: Uri, name: String): String {
        return try {
            //создаём экземпляр класса File, который указывает на нужный каталог
            val filePath = File(
                context.getExternalFilesDir(
                    Environment.DIRECTORY_PICTURES
                ), PACKAGE_NAME
            )

            //создаем каталог, если он не создан
            if (!filePath.exists()) filePath.mkdirs()

            //создаём экземпляр класса File, который указывает на файл внутри каталога
            val file = File(filePath, "${name.toLatin()}.jpg")

            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    BitmapFactory
                        .decodeStream(inputStream)
                        ?.compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
                }
            }

            file.absolutePath
        } catch (e: Exception) {
            Log.d("Exeption", "saveImage: ${e.stackTrace}")
            ""
        }
    }

    override fun getImage(name: String): Uri {
        val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), PACKAGE_NAME)
        val file = File(filePath, "$name.jpg")
        return file.toUri()
    }

    override fun getAllImages(): List<Uri> {
        return File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
            .listFiles()?.map { it.toUri() } ?: emptyList()
    }

    override fun deleteAllImages() {
        try {
            val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), PACKAGE_NAME)

            if (!filePath.exists()) {
                Log.d("ImageStorage", "Directory does not exist")
            }

            val files = filePath.listFiles()
            if (files.isNullOrEmpty()) {
                Log.d("ImageStorage", "No files to delete")
            }

            var allDeleted = true
            files.forEach { file ->
                if (file.isFile) {
                    val deleted = file.delete()
                    if (!deleted) {
                        allDeleted = false
                        Log.d("ImageStorage", "Failed to delete: ${file.name}")
                    }
                }
            }

            if (allDeleted) {
                Log.d("ImageStorage", "All images deleted successfully")
            } else {
                Log.d("ImageStorage", "Some images failed to delete")
            }

            allDeleted
        } catch (e: Exception) {
            Log.d("Exception", "deleteAllImages: ${e.stackTrace}")
        }
    }

    override fun deleteImage(imagePath: String) {
        try {
            if (imagePath.isEmpty()) {
                Log.d("ImageStorage", "Image path is empty")
            }

            val file = File(imagePath)

            if (file.exists()) {
                val deleted = file.delete()
                if (deleted) {
                    Log.d("ImageStorage", "Image deleted: $imagePath")
                } else {
                    Log.d("ImageStorage", "Failed to delete image: $imagePath")
                }
                deleted
            } else {
                Log.d("ImageStorage", "Image not found: $imagePath")
            }
        } catch (e: Exception) {
            Log.d("Exception", "deleteImage: ${e.stackTrace}")
        }
    }

    companion object {
        const val PACKAGE_NAME = "myalbum"
    }
}