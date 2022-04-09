package com.michaelpohl.wifitool.shared

@Suppress("MagicNumber")
fun Long.millisToMinutes(): Long = this / 60000
@Suppress("MagicNumber")
fun Long.minutesToMillis(): Long = this * 60000
@Suppress("MagicNumber")
fun Float.millisToMinutes(): Float = this / 60000
@Suppress("MagicNumber")
fun Float.minutesToMillis(): Float = this * 60000
