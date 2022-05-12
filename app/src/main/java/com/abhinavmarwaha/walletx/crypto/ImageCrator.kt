package com.abhinavmarwaha.walletx.crypto

import android.os.CountDownTimer
import com.abhinavmarwaha.walletx.utils.FileUtils
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.*
import javax.crypto.spec.SecretKeySpec

object ImageCryptor {

    private const val TAG = "ImageCrypter"

    //Algorithm
    private const val ALGORITHM = "AES"

    //encryption variables
    private var key: SecretKey? = null

    // 128-Bit Key
    private var salt = "A8768CC5BEAA6093"

    //Image Name
    const val TEMP_IMAGE_TAG = "temp_"

    init {
        // Get key
        key = getKey()
    }

    //Keep image for 5 seconds
    private fun deleteFileAfterView(path: String) {
        val timer = object : CountDownTimer(20000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                deleteFile(path)
            }
        }
        timer.start()
    }

    fun getDecryptedImageIfExists(originalFilePath: String): Pair<Boolean, String> {

        val filePath = getImageParentPath(originalFilePath)
        val imageName = getImageNameFromPath(originalFilePath)
        val file = File(filePath, "$TEMP_IMAGE_TAG$imageName")

        return if (file.exists()) {
            Pair(true, file.path)
        } else {
            Pair(false, file.path)
        }
    }

    suspend fun encryptImageList(imagesList: ArrayList<String>): List<File> {

        val listSize = imagesList.size
        var count = 0

        val listOfEncryptedFiles = arrayListOf<File>()
        imagesList.forEach {
            listOfEncryptedFiles.add(encryptImage(it))
        }

        return listOfEncryptedFiles

    }

    suspend fun encryptImage(originalFilePath: String): File {

        val encryptedImagePath = createCopyOfOriginalFile(originalFilePath)
        try {
            val fis = FileInputStream(originalFilePath)
            val aes = Cipher.getInstance(ALGORITHM)
            aes.init(Cipher.ENCRYPT_MODE, key)
            val fs = FileOutputStream(File(encryptedImagePath))
            val out = CipherOutputStream(fs, aes)
            out.write(fis.readBytes())
            out.flush()
            out.close()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        } catch (e: IllegalBlockSizeException) {
            e.printStackTrace()
        } catch (e: BadPaddingException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //Delete original file
        deleteFile(originalFilePath)

        //Rename encrypted image file to original name
        val createdFile = renameImageToOriginalFileName(encryptedImagePath)
        return File(createdFile)

    }

    suspend fun decryptImage(originalFilePath: String): File {


        val decryptedFilePath = createCopyOfOriginalFile(originalFilePath)

        val fis = FileInputStream(originalFilePath)

        try {
            val aes = Cipher.getInstance(ALGORITHM)
            aes.init(Cipher.DECRYPT_MODE, key)
            val out = CipherInputStream(fis, aes)

            File(decryptedFilePath).outputStream().use {
                out.copyTo(it)
            }

        } catch (ex: NoSuchAlgorithmException) {
            ex.printStackTrace()


        } catch (ex: NoSuchPaddingException) {
            ex.printStackTrace()


        } catch (ex: InvalidKeyException) {
            ex.printStackTrace()


        } catch (ex: IOException) {
            ex.printStackTrace()


        }
        return File(decryptedFilePath)
    }

    suspend fun decryptFiles(lisOfEncryptedFiles: ArrayList<String>): List<File> {

        val listOfDecryptedFiles = arrayListOf<File>()
        lisOfEncryptedFiles.forEach {
            listOfDecryptedFiles.add(decryptImage(it))
        }

        return listOfDecryptedFiles
    }

    private fun renameImageToOriginalFileName(path: String): String {
        val filePath = getImageParentPath(path)
        val imageName = getImageNameFromPath(path)

        val from = File(filePath, imageName)

        val renameTo = imageName!!.replace(TEMP_IMAGE_TAG, "")

        val to = File(filePath, renameTo)
        if (from.exists())
            from.renameTo(to)

        return to.path
    }

    private fun createCopyOfOriginalFile(originalFilePath: String): String {

        val filePath = getImageParentPath(originalFilePath)
        val imageName = getImageNameFromPath(originalFilePath)

        val originalFile = File(originalFilePath)
        val copyFile = File(filePath, "$TEMP_IMAGE_TAG$imageName")

        //Create a copy of original file
        try {
            FileUtils.copy(originalFile, copyFile)
        } catch (ex: IOException) {
            ex.printStackTrace()
        }

        return copyFile.path
    }

    private fun deleteFile(path: String) {
        val file = File(path)

        if (file.exists())
            file.delete()
    }

    private fun getImageParentPath(path: String?): String? {
        var newPath = ""
        path?.let {
            newPath = it.substring(0, it.lastIndexOf("/") + 1)
        }
        return newPath
    }

    private fun getImageNameFromPath(path: String?): String? {
        var newPath = ""
        path?.let {
            newPath = it.substring(it.lastIndexOf("/") + 1)
        }
        return newPath
    }

    private fun getKey(): SecretKey? {

        var secretKey: SecretKey? = null

        try {
            secretKey = SecretKeySpec(salt.toBytes(), ALGORITHM)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

        return secretKey
    }

    /*
     * This will convert string to byte array.
     */
    private fun String.toBytes(): ByteArray {
        return this.toByteArray(Charsets.UTF_8)
    }
}
