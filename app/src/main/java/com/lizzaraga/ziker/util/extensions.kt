package com.lizzaraga.ziker.util

import kotlin.math.ceil
import kotlin.math.floor

fun Long.toMinutes(): String{
    fun format(value: Int): String{
        if(value < 10) return "0$value"
        return value.toString()
    }

    var minutes = 0
    var seconds = 0
    val totalSeconds = this / 1000
    minutes = floor((totalSeconds / 60).toDouble()).toInt()
    val remainingSeconds = totalSeconds % 60
    seconds = ceil((remainingSeconds).toDouble()).toInt()

    return "${format(minutes)}:${format(seconds)}"
}

fun Long.toMb(): Double{
    return String.format("%.2f", (this / (1_000_000F))).toDouble()
}