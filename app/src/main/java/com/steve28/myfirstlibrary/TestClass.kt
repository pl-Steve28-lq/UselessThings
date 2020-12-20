package com.steve28.myfirstlibrary

import android.util.Log
import com.steve28.uselessthings.annotations.Chaining

@Chaining
class TestClass(private val TAG: String) {
    fun asdf() { Log.d(TAG, "Test1") }
    fun qwer() { Log.d(TAG, "Test2") }
    fun zxcv() { Log.d(TAG, "Test3") }
}