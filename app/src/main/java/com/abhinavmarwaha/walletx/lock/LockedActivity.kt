package com.abhinavmarwaha.walletx.lock

import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.abhinavmarwaha.walletx.R


class LockedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pattern_default)
        findViewById<PatternLockView>(R.id.defaultPatternLockView).setOnPatternListener(listener)
    }

    private var listener  = object : PatternLockView.OnPatternListener {

        override fun onStarted() {
            super.onStarted()
        }

        override fun onProgress(ids: ArrayList<Int>) {
            super.onProgress(ids)
        }

        override fun onComplete(ids: ArrayList<Int>): Boolean {
            var isCorrect = TextUtils.equals("012", getPatternString(ids))
            var tip: String
            if (isCorrect) {
                tip = "correct:" + getPatternString(ids)
                finishActivity()
            } else {
                tip = "error:" + getPatternString(ids)
            }
            //Toast.makeText(this@PatternLockActivity, tip, Toast.LENGTH_SHORT).show()
            return isCorrect
        }
    }

    fun finishActivity(){
        this.finish()
    }

    private fun getPatternString(ids: ArrayList<Int>) : String {
        var result = ""
        for (id in ids) {
            result += id.toString()
        }
        return result
    }
}