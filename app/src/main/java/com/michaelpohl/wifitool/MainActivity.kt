package com.michaelpohl.wifitool

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.michaelpohl.wifiservice.MonitoringService
import com.michaelpohl.wifiservice.MonitoringServiceConnection
import com.michaelpohl.wifitool.common.util.CallbackTimberTree
import com.michaelpohl.wifitool.ui.theme.WifiToolTheme
import timber.log.Timber
import java.io.IOException

class MainActivity : ComponentActivity() {

    private val serviceConnection = MonitoringServiceConnection(this::class.java).apply {
        onServiceConnectedListener = { Timber.d("Connected!") }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initTimber()
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

    private fun initTimber() {
        if (Timber.forest().size < 1) {
            Timber.plant(CallbackTimberTree { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() })
        }
        Timber.d("Timber is on")
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
    }

}
