package com.abhinavmarwaha.walletx.utils

import android.nfc.NdefRecord
import java.nio.charset.Charset

fun nfc(){
    val mimeRecord = NdefRecord.createMime(
        "application/vnd.com.example.android.beam",
        "Beam me up, Android".toByteArray(Charset.forName("US-ASCII"))
    )

}