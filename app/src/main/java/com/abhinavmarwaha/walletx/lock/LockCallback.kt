package com.abhinavmarwaha.walletx.lock

interface LockCallback {
    fun onStart() {}
    fun onEnd(result: ArrayList<Int>, isCorrect:Boolean) {}
    fun onProgress(index: Int) {}
}