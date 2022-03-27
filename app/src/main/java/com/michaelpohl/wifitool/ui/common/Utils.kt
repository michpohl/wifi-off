package com.michaelpohl.wifitool.ui.common

@Suppress("MagicNumber")
fun Long.millisToMinutes(): Long = this / 60000
@Suppress("MagicNumber")
fun Long.minutesToMillis(): Long = this * 60000
