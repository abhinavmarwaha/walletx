package com.abhinavmarwaha.walletx.lock

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.abhinavmarwaha.walletx.R
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import io.github.osipxd.datastore.encrypted.createEncrypted
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File


const val PASS = "Pass"

class LockedActivity : AppCompatActivity() {
    var dataStore: DataStore<Preferences>? = null
    var setPattern = false
    var pattern = ""
    private val key = stringPreferencesKey(PASS)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setPattern = intent.getBooleanExtra("setPattern", false);
        val path:String = this.filesDir.path

        CoroutineScope(Dispatchers.Default).launch {
            AeadConfig.register()

            val aead = AndroidKeysetManager.Builder()
                .withSharedPref(this@LockedActivity, "master_keyset", "master_key_preference")
                .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
                .withMasterKeyUri("android-keystore://master_key")
                .build()
                .keysetHandle
                .getPrimitive(Aead::class.java)
            dataStore = PreferenceDataStoreFactory.createEncrypted(aead) {
                File(path, "pattern.preferences_pb")
            }

            dataStore!!.data
                .map { preferences ->
                    preferences[key]
                }.collect{
                    pattern = it ?: ""
                }
        }

        setContentView(R.layout.activity_pattern_default)
        findViewById<PatternLockView>(R.id.defaultPatternLockView).setOnPatternListener(listener)
    }

    suspend fun setPatternString(pattern: String) {
        dataStore!!.edit { store ->
            store[key] = pattern
        }
    }

    private var listener = object : PatternLockView.OnPatternListener {

        override fun onStarted() {
            super.onStarted()
        }

        override fun onProgress(ids: ArrayList<Int>) {
            super.onProgress(ids)
        }

        override fun onComplete(ids: ArrayList<Int>): Boolean {
            var isCorrect: Boolean

            if (setPattern) {
                val pattern = getPatternString(ids)
                CoroutineScope(Dispatchers.IO).launch {
                    setPatternString(pattern)
                }
                Toast.makeText(this@LockedActivity, "Pattern Stored", Toast.LENGTH_SHORT).show()
                isCorrect = true
                finishActivity()
            } else {
                isCorrect = TextUtils.equals(pattern, getPatternString(ids))
                val tip: String
                if (isCorrect) {
                    tip = "correct:" + getPatternString(ids)
                    finishActivity()
                } else {
                    tip = "error:" + getPatternString(ids)
                }
                Toast.makeText(this@LockedActivity, tip, Toast.LENGTH_SHORT).show()
            }

            return isCorrect
        }
    }

    fun finishActivity() {
        this.finish()
    }

    private fun getPatternString(ids: ArrayList<Int>): String {
        var result = ""
        for (id in ids) {
            result += id.toString()
        }
        return result
    }
}