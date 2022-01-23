package com.michaelpohl.wifiservice.storage

fun main() {
    var counter = 0

    val yearlyAmount = 12000
    val returnPercent = 6

    var currentAmount = 0

    while ( counter < 25) {
        currentAmount += yearlyAmount
        val profit = currentAmount / 100 * returnPercent
        println ("$returnPercent% returns on the current savings is $profit")
        currentAmount += profit
        counter++
        println("After $counter years: $currentAmount")
        println("Money put in: ${counter * yearlyAmount}. Money returned from investment: ${currentAmount - (counter * yearlyAmount)}")
    }
}
