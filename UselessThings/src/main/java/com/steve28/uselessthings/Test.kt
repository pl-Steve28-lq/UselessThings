package com.steve28.uselessthings

import com.steve28.uselessthings.annotations.*
import com.steve28.uselessthings.extensions.*
import com.steve28.uselessthings.utilities.*

fun main() {
    val a = Json.loads("{\"1\": 2, \"3\": [1, 2]}")
    println(a["1"])
    println(a["3"])
}