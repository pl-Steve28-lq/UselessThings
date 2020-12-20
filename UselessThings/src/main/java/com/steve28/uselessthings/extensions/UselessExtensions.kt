package com.steve28.uselessthings.extensions

import java.util.regex.Pattern
import kotlin.math.min

fun String . isAlphabet() = Pattern.matches("[a-zA-Z]", this)
fun String . isHangul() = Pattern.matches("[ㄱ-ㅎㅏ-ㅣ가-힣]", this)
fun Char . isHangul() = this.toString().isHangul()
fun String . isHangulChosung() = Pattern.matches("[ㄱ-ㅎ]", this)
fun Char . isHangulChosung() = this.toString().isHangulChosung()
fun String . isHangulJungsung() = Pattern.matches("[ㅏ-ㅣ]", this)
fun Char . isHangulJungsung() = this.toString().isHangulJungsung()
fun String . isHangulJongsung() = " ㄳㄵㄶㄺㄻㄼㄽㄾㄿㅀㅄ".toArray().contains(this)
fun Char . isHangulJongsung() = this.toString().isHangulJongsung()

fun Char . toUnicode() = this.toInt()
fun String . toUnicode() = this.map { v -> v.toUnicode() }
fun Int . toUnicode() = this.toChar()

fun String . toArray() = this.toCharArray().map { it.toString() }.toTypedArray()

fun Int . bin() = this.radix(2)
fun Int . oct() = this.radix(8)
fun Int . hex() = this.radix(16)
fun Int . radix(radix: Int) = this.toString(radix)

fun String . decompose(): String {
    /*
    Original code from
    https://github.com/pl-Steve28-lq/ProgrammingLanguages/blob/master/Python/HangeulSeperater.py
    */

    val pc = "ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎ".toArray()
    val mc = "ㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣ".toArray()
    val lc = " ㄱㄲㄳㄴㄵㄶㄷㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅄㅅㅆㅇㅈㅊㅋㅌㅍㅎ".toArray()
    val base = 44032
    val ch = 588
    val js = 28

    return this.toArray().map {
        if (!it.isHangul()) it
        else {
            val chars = it.toUnicode()[0] - base
            val ch1 = chars/ch
            val ch2 = (chars - ch*ch1)/js
            val ch3 = chars - ch*ch1 - js*ch2
            try {
                pc[ch1] + mc[ch2] + (if (lc[ch3] != " ") lc[ch3] else "")
            }
            catch (e: Exception) { it }
        }
    }.joinToString("")
}

fun String . compose(): String {
    var res = ""

    val pc = "ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎ".toArray()
    val mc = "ㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣ".toArray()
    val lc = " ㄱㄲㄳㄴㄵㄶㄷㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅄㅅㅆㅇㅈㅊㅋㅌㅍㅎ".toArray()
    val base = 44032
    val ch = 588
    val js = 28

    val arr = this.toMutableList()
    var idx = 0
    while (idx < arr.size) {
        val cho = arr[idx].toString()
        val jung = if (idx+1 < arr.size) arr[idx+1].toString() else " "
        val jong = if (idx+2 < arr.size) arr[idx+2].toString() else " "
        val nextcho = if (idx+3 < arr.size) arr[idx+3].toString() else " "

        if (!cho.isHangul()) {
            res += cho
            idx += 1
            continue
        }
        if (cho.isHangulChosung() &&
            jung.isHangulJungsung()
        ) {
            val jump: Int
            var isJong = true
            var isValid = true
            if (jong.isHangulJongsung() ||
                jong.isHangulChosung() && nextcho.isHangulChosung()
            ) {
                jump = 3
            } else if (jong.isHangulChosung() && nextcho.isHangulJungsung()) {
                jump = 2
                isJong = false
            } else {
                jump = 1; isValid = false
            }
            val han = if (isValid)
                (base + ch * pc.indexOf(cho) + js * mc.indexOf(jung) + if (isJong) lc.indexOf(jong) else 0).toUnicode()
            else cho
            res += han
            idx += jump
        } else {
            res += cho
            idx += 1
        }
    }

    return res
}

fun equality(th: String, other: String): Double {
    var all = 0
    var good = 0
    val t = th.decompose().toArray()
    val o = other.decompose().toArray()
    for (i in 0 until min(t.size, o.size)) {
        if (t[i] == o[i]) good += 1
        all += 1
    }
    return good.toDouble() / all.toDouble()
}