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


class ImageStorage(
    private val context: Context
) {

    fun saveImage(uri: Uri, name: String): String {
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

    fun getImage(name: String): Uri {
        val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), PACKAGE_NAME)
        val file = File(filePath, "$name.jpg")
        return file.toUri()
    }

    fun getAllImages(): List<Uri> {
        return File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
            .listFiles()?.map { it.toUri() } ?: emptyList()
    }

    fun deleteAllImages(): Boolean {
        val directory = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), PACKAGE_NAME)

        return if (directory.exists() && directory.isDirectory) {
            val files = directory.listFiles()
            var allDeleted = true

            files?.forEach { file ->
                if (!file.delete()) {
                    allDeleted = false
                }
            }
            allDeleted
        } else {
            false
        }
    }

    companion object {
        const val PACKAGE_NAME = "myalbum"
    }
}