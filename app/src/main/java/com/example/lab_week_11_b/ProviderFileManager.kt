package com.example.lab_week_11_b

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import org.apache.commons.io.IOUtils
import java.io.File
import java.util.concurrent.Executor

class ProviderFileManager(
    private val context: Context,
    private val fileHelper: FileHelper,
    private val contentResolver: ContentResolver,
    private val executor: Executor,
    private val mediaContentHelper: MediaContentHelper
) {

    // Generate FileInfo for photo
    fun generatePhotoUri(time: Long): FileInfo {
        val name = "img_$time.jpg"

        val file = File(
            context.getExternalFilesDir(fileHelper.getPicturesFolder()),
            name
        )

        return FileInfo(
            fileHelper.getUriFromFile(file),
            file,
            name,
            fileHelper.getPicturesFolder(),
            "image/jpeg"
        )
    }

    // Generate FileInfo for video
    fun generateVideoUri(time: Long): FileInfo {
        val name = "video_$time.mp4"

        val file = File(
            context.getExternalFilesDir(fileHelper.getVideosFolder()),
            name
        )

        return FileInfo(
            fileHelper.getUriFromFile(file),
            file,
            name,
            fileHelper.getVideosFolder(),
            "video/mp4"
        )
    }

    // Insert image into MediaStore
    fun insertImageToStore(fileInfo: FileInfo?) {
        fileInfo?.let {
            insertToStore(
                it,
                mediaContentHelper.getImageContentUri(),
                mediaContentHelper.generateImageContentValues(it)
            )
        }
    }

    // Insert video into MediaStore
    fun insertVideoToStore(fileInfo: FileInfo?) {
        fileInfo?.let {
            insertToStore(
                it,
                mediaContentHelper.getVideoContentUri(),
                mediaContentHelper.generateVideoContentValues(it)
            )
        }
    }

    // Copy file into MediaStore location
    private fun insertToStore(
        fileInfo: FileInfo,
        contentUri: Uri,
        contentValues: ContentValues
    ) {
        executor.execute {
            val insertedUri = contentResolver.insert(contentUri, contentValues)

            insertedUri?.let {
                val input = contentResolver.openInputStream(fileInfo.uri)
                val output = contentResolver.openOutputStream(insertedUri)
                IOUtils.copy(input, output)
            }
        }
    }
}
