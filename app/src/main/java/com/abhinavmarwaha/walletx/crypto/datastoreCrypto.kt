package com.abhinavmarwaha.walletx.crypto

import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

/*
    Reference : https://stackoverflow.com/questions/65653650/androidx-datastore-aes-cbc-pkcs7-javax-crypto-illegalblocksizeexception
 */

interface CipherProvider {
    val encryptCipher: Cipher
    fun decryptCipher(iv: ByteArray): Cipher
}

@RequiresApi(Build.VERSION_CODES.M)
class AesCipherProvider constructor(
    private val keyName: String,
    private val keyStore: KeyStore,
    private val keyStoreName: String
) : CipherProvider {

    override val encryptCipher: Cipher
        get() = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, getOrCreateKey())
        }

    override fun decryptCipher(iv: ByteArray): Cipher =
        Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, getOrCreateKey(), IvParameterSpec(iv))
        }


    private fun getOrCreateKey(): SecretKey =
        (keyStore.getEntry(keyName, null) as? KeyStore.SecretKeyEntry)?.secretKey
            ?: generateKey()

    private fun generateKey(): SecretKey =
        KeyGenerator.getInstance(ALGORITHM, keyStoreName)
            .apply { init(keyGenParams) }
            .generateKey()

    private val keyGenParams =
        KeyGenParameterSpec.Builder(
            keyName,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).apply {
            setBlockModes(BLOCK_MODE)
            setEncryptionPaddings(PADDING)
            setUserAuthenticationRequired(false)
            setRandomizedEncryptionRequired(true)
        }.build()

    companion object {
        const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
        const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
    }
}

interface Crypto {
    fun encrypt(rawBytes: ByteArray, outputStream: OutputStream)
    fun decrypt(inputStream: InputStream): ByteArray
}

class CryptoImpl constructor(private val cipherProvider: CipherProvider) : Crypto {

    override fun encrypt(rawBytes: ByteArray, outputStream: OutputStream) {
        val cipher = cipherProvider.encryptCipher
        val encryptedBytes = cipher.doFinal(rawBytes)
        with(outputStream) {
            write(cipher.iv.size)
            write(cipher.iv)
            write(encryptedBytes.size)
            write(encryptedBytes)
        }
    }

    override fun decrypt(inputStream: InputStream): ByteArray {
        val ivSize = inputStream.read()
        val iv = ByteArray(ivSize)
        inputStream.read(iv)
        val encryptedDataSize = inputStream.read()
        val encryptedData = ByteArray(encryptedDataSize)
        inputStream.read(encryptedData)
        val cipher = cipherProvider.decryptCipher(iv)
        return cipher.doFinal(encryptedData)
    }
}

object SecurityModule {

    const val KEY_NAME = "Key Name"
    const val KEY_STORE_NAME = "Key Store Name"

    private const val ANDROID_KEY_STORE_TYPE = "AndroidKeyStore"
    private const val SIMPLE_DATA_KEY_NAME = "SimpleDataKey"

    fun provideKeyStore(): KeyStore =
        KeyStore.getInstance(ANDROID_KEY_STORE_TYPE).apply { load(null) }

    fun providesKeyName(): String = SIMPLE_DATA_KEY_NAME

    fun providesKeyStoreName(): String = ANDROID_KEY_STORE_TYPE


    interface Declarations {

        fun bindsCipherProvider(impl: AesCipherProvider): CipherProvider

        fun bindsCrypto(impl: CryptoImpl): Crypto
    }
}

class SecureSimpleDataSerializer(private val crypto: Crypto) :
    Serializer<Preferences> {

    override fun readFrom(input: InputStream): Preferences {
        return if (input.available() != 0) {
            try {

                Preferences.ADAPTER.decode(crypto.decrypt(input))
            } catch (exception: IOException) {
                throw CorruptionException("Cannot read proto", exception)
            }
        } else {
            Preferences()
        }
    }

    override fun writeTo(t: SimpleData, output: OutputStream) {
        crypto.encrypt(SimpleData.ADAPTER.encode(t), output)
    }
}

object DataStoreModule {

    fun providesDataStore(
        context: Context,
        crypto: Crypto
    ): DataStore<Preferences> =
        DataStoreFactory.create(
            serializer = SecureSimpleDataSerializer(crypto), // your Serializer
            corruptionHandler = null,
            migrations = emptyList(),
            scope = CoroutineScope(Dispatchers.IO + Job())
        )
//        PreferenceDataStoreFactory.create()
        {
            File(context.filesDir, "DataStoreTest.pb")
        }
}