package com.submission.mystoryapp.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Locale

object MediaUtils {
    private const val FILE_FORMAT = "dd-MMM-yyyy"

    private val timeStamp: String = SimpleDateFormat(
        FILE_FORMAT,
        Locale.US
    ).format(System.currentTimeMillis())

    fun uriToFile(imgSelected: Uri, context: Context): File {
        val content: ContentResolver = context.contentResolver
        val file = createTemporaryFile(context)
        val input = content.openInputStream(imgSelected) as InputStream
        val output: OutputStream = FileOutputStream(file)
        var len: Int
        val buff = ByteArray(1024)
        while (input.read(buff).also { len = it } > 0) output.write(buff, 0, len)
        output.close()
        input.close()
        return file
    }

    fun createTemporaryFile(context: Context): File {
        val storageFile: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(timeStamp, ".jpg", storageFile)
    }

    fun rotateBitmap(bitmap: Bitmap, isBackCamera: Boolean = false): Bitmap {
        val matrix = Matrix()
        return if (isBackCamera) {
            matrix.postRotate(90f)
            Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                matrix,
                true
            )
        } else {
            matrix.postRotate(-90f)
            matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
            Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                matrix,
                true
            )
        }
    }

    fun reduceFileImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressValue = 100
        var lenStream: Int

        do {
            val bitmapStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressValue, bitmapStream)
            val bitmapByteArray = bitmapStream.toByteArray()
            lenStream = bitmapByteArray.size
            compressValue -= 5
        } while (lenStream > 1000000)

        bitmap.compress(Bitmap.CompressFormat.JPEG, compressValue, FileOutputStream(file))

        return file
    }
}