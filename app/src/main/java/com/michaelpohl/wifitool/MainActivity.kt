package com.michaelpohl.wifitool

import android.content.Intent
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
import com.michaelpohl.service.MonitoringService
import com.michaelpohl.service.MonitoringServiceConnection
import com.michaelpohl.wifitool.ui.theme.WifiToolTheme
import timber.log.Timber
import java.io.IOException

class MainActivity : ComponentActivity() {

    private val serviceConnection = MonitoringServiceConnection(this::class.java).apply {
        onServiceConnectedListener = { Timber.d("Connected!")}
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.plant(Timber.DebugTree())
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

    override fun onResume() {
        super.onResume()
        Timber.d("onResume")
        bindService(
            Intent(this, MonitoringService::class.java),
            serviceConnection,
            BIND_AUTO_CREATE
        )
    }

    override fun onPause() {
        super.onPause()
        Timber.d("should unbind")
        unbindService(serviceConnection)
    }

    override fun onDestroy() {
        Timber.d("onDestroy")
        super.onDestroy()
        stopService(Intent(this@MainActivity, MonitoringService::class.java))
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
