package com.michaelpohl.wifitool.shared

fun largerOf(first: Int, second: Int) : Int {
    return if (first > second) first else second
}

fun largerOf(first: Long, second: Long) : Long {
    return if (first > second) first else second
}
