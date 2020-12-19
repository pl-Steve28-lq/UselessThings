package com.steve28.myfirstlibrary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.steve28.uselessthings.extensions.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, "안녕하세요".decompose())
        Log.d(TAG, "ㅇㅏㄴㄴㅕㅇㅎㅏㅅㅔㅇㅛ".compose())
    }

    companion object {
        const val TAG = "TestLog"
    }
}