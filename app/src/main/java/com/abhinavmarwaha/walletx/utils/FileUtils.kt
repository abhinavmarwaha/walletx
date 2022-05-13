package com.abhinavmarwaha.walletx.utils

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import java.io.*


object FileUtils {

    private const val TAG = "FileUtils"

    private const val appFolder = "WalletX"
    private const val imagesFolder = "Images"

    private val appStoragePath = (Environment.getExternalStorageDirectory().toString() + File.separator + appFolder
            + File.separator)
    private val imagesPath = appStoragePath + imagesFolder + File.separator

    @Throws(IOException::class)
    fun copy(sourceLocation: File, targetLocation: File) {
        if (sourceLocation.isDirectory) {
            copyDirectory(sourceLocation, targetLocation)
        } else {
            copyFile(sourceLocation, targetLocation)
        }
    }

    @Throws(IOException::class)
    private fun copyDirectory(source: File, target: File) {
        if (!target.exists()) {
            target.mkdir()
        }

        for (f in source.list()) {
            copy(File(source, f), File(target, f))
        }
    }

    @Throws(IOException::class, FileNotFoundException::class)
    private fun copyFile(source: File, target: File) {
//        try {
            val inputStream = FileInputStream(source)
            val out = FileOutputStream(target)
            out.write(inputStream.readBytes())
            out.flush()
            out.close()
//        } catch (e: IOException) {
//            throw IOException()
//        }
    }

    @Throws(IOException::class)
    private fun copyFile(inputStream: InputStream, out: OutputStream) {
        out.write(inputStream.readBytes())
        out.flush()
        out.close()
    }

    fun save(file: File, data: ByteArray, bmp: Bitmap?): File {

        try {
            FileOutputStream(file).use { os ->

                bmp?.compress(Bitmap.CompressFormat.JPEG, 100, os) ?: os.write(data)
                os.flush()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // Ignore
        return file
    }

    fun createDirIfNotExists(path: String): Boolean {
        var ret = true
        val file = File(path)
        if (!file.exists()) {
            if (!file.mkdirs()) {
                ret = false
            }
        }
        return ret
    }

    fun deleteFile(path: String): Boolean {
        val f = File(path)

        return if (f.exists()) {
            f.delete()
        } else false

    }

    fun deleteFile(f: File): Boolean {

        return if (f.exists()) {
            f.delete()
        } else false

    }

    fun deleteDir(fileOrDirectory: File) {
        if (fileOrDirectory.isDirectory)
            for (child in fileOrDirectory.listFiles())
                deleteDir(child)
        fileOrDirectory.delete()
    }

    fun copyAssets(context: Context) {
        val assetManager = context.assets
        var files: Array<String>? = null

        try {
            files = assetManager.list("")
        } catch (e: IOException) {
            Log.e("tag", "Failed to get asset file list.", e)
        }

        if (files != null)
            for (filename in files) {
                val f = File(filename)

                //Make sure it's image file
                if (f.name.contains("image_")) {
                    var inputStream: InputStream? = null
                    var out: OutputStream? = null
                    try {
                        inputStream = assetManager.open(filename)
                        val outFile = File(imagesPath, filename)
                        out = FileOutputStream(outFile)
                        copyFile(inputStream, out)
                    } catch (e: IOException) {
                        Log.e("tag", "Failed to copy asset file: $filename", e)
                    } finally {
                        if (inputStream != null) {
                            try {
                                inputStream.close()
                            } catch (e: IOException) {
                                // NOOP
                            }
                        }
                        if (out != null) {
                            try {
                                out.close()
                            } catch (e: IOException) {
                                // NOOP
                            }
                        }
                    }
                }
            }
    }
}