package com.steve28.myfirstlibrary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.steve28.uselessthings.extensions.*
import com.uselessthings.generated.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, "안녕하세요".decompose())
        Log.d(TAG, "ㅇㅏㄴㄴㅕㅇㅎㅏㅅㅔㅇㅛ".compose())
        TestClass(TAG)._asdf(TAG)._qwer()._zxcv()
    }

    companion object {
        const val TAG = "TestLog"
    }
}