package com.abhinavmarwaha.walletx.crypto

import android.R.attr.bitmap
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import androidx.security.crypto.EncryptedFile
import com.abhinavmarwaha.walletx.models.globalState
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.security.InvalidKeyException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.crypto.*


class ImageCryptor(private val keyString: String) {
//    private val ALGORITHM = "AES"
//
//    private var key: SecretKey? = null

//    init {
//        key = getKey(keyString)
//    }

//    private fun deleteFileAfterView(path: String) {
//        val timer = object : CountDownTimer(20000, 1000) {
//            override fun onTick(millisUntilFinished: Long) {}
//
//            override fun onFinish() {
//                deleteFile(path)
//            }
//        }
//        timer.start()
//    }

    companion object{
        @Throws(NoSuchAlgorithmException::class)
        fun SHAsum(convertme: ByteArray?): String {
            val md: MessageDigest = MessageDigest.getInstance("SHA-1")
            return byteArray2Hex(md.digest(convertme))
        }

        private fun byteArray2Hex(hash: ByteArray): String {
            val formatter = Formatter()
            for (b in hash) {
                formatter.format("%02x", b)
            }
            return formatter.toString()
        }
    }

    fun encryptBitmap(originalBitmap: Bitmap, context: Context): String {
        val bos = ByteArrayOutputStream()
        originalBitmap.compress(CompressFormat.PNG, 0 /*ignored for PNG*/, bos)
        val bitmapdata: ByteArray = bos.toByteArray()

        val hash = SHAsum(bitmapdata)
        try {
            val secretFile = File(context.filesDir, hash)
            if(secretFile.exists())  secretFile.delete()
            val encryptedFile = EncryptedFile.Builder(
                secretFile,
                context,
                SHAsum(globalState.pattern!!.toByteArray()),
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB)
                .build()

            encryptedFile.openFileOutput().use { outputStream ->
                outputStream.write(bitmapdata)
                outputStream.flush()
            }
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

        return hash

    }

    fun decryptBitmap(hash: String, context: Context): ByteArray? {
        try {
            val secretFile = File(context.filesDir, hash)
            val encryptedFile = EncryptedFile.Builder(
                secretFile,
                context,
                SHAsum(globalState.pattern!!.toByteArray()),
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB)
                .build()

            encryptedFile.openFileInput().use { inputStream ->
              return inputStream.readBytes()
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

        return null
    }

//    @Throws(java.lang.Exception::class)
//    fun getKey(password: String): SecretKey {
//        val keyStart = password.toByteArray(charset("UTF-8"))
//        val kgen = KeyGenerator.getInstance("AES")
//        val sr: SecureRandom = SecureRandom.getInstance("SHA1PRNG")
//        sr.setSeed(keyStart)
//        kgen.init(128, sr)
//        val skey = kgen.generateKey()
//        return skey
//    }
}