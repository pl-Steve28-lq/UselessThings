package com.steve28.myfirstlibrary

import android.util.Log
import com.steve28.uselessthings.annotations.Chaining

@Chaining
class TestClass(private val TAG: String) {
    fun asdf(msg: String) { Log.d(TAG, msg) }
    fun qwer() { Log.d(TAG, "Test2") }
    fun zxcv() { Log.d(TAG, "Test3") }
}