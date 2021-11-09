package com.michaelpohl.wifitool

import android.icu.lang.UCharacter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.michaelpohl.wifitool.ui.theme.WifiToolTheme
import java.io.IOException

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            Runtime.getRuntime().exec("su")

        } catch (e: IOException) {

        }
        setContent {
            WifiToolTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Column() {

                        Button(onClick = { turnWifiOff() }) {
                            Text("Off")
                        }
                        Button(onClick = { turnWifiOn() }) {
                            Text("On")

                        }
                    }
                }
            }
        }
    }

    private fun turnWifiOff() {
        try {
            Runtime.getRuntime().exec("su -c svc wifi disable")

        } catch (e: IOException) {

        }
    }

    private fun turnWifiOn() {
        try {
            Runtime.getRuntime().exec("su -c svc wifi enable")

        } catch (e: IOException) {

        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    WifiToolTheme {
        Greeting("Android")
    }
}
