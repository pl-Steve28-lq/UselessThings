package com.steve28.myfirstlibrary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.steve28.uselessthings.extensions.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        L("안녕하세요".decompose())
        L("ㅇㅏㄴㄴㅕㅇㅎㅏㅅㅔㅇㅛ".compose())
    }

    companion object {
        const val TAG = "TestLog"
    }

    private fun L(a: String) = Log.d(TAG, a)
}