package com.abhinavmarwaha.walletx.archmodel

import android.content.SharedPreferences
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

class SettingsStore(override val di: DI) : DIAware {
    private val sp: SharedPreferences by instance()

//    private val _showOnlyUnread = MutableStateFlow(sp.getBoolean(PREF_SHOW_ONLY_UNREAD, true))
//    val showOnlyUnread: StateFlow<Boolean> = _showOnlyUnread.asStateFlow()
//    fun setShowOnlyUnread(value: Boolean) {
//        sp.edit().putBoolean(PREF_SHOW_ONLY_UNREAD, value).apply()
//        _showOnlyUnread.value = value
//    }

}


/**
 * Image settings
 */
const val PREF_IMG_ONLY_WIFI = "pref_img_only_wifi"
const val PREF_IMG_SHOW_THUMBNAILS = "pref_img_show_thumbnails"