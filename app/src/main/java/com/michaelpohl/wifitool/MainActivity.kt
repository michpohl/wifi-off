package com.michaelpohl.wifitool

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import com.michaelpohl.wifiservice.MonitoringService
import com.michaelpohl.wifiservice.MonitoringServiceConnection
import com.michaelpohl.wifiservice.looper.MonitoringLooper
import com.michaelpohl.wifitool.common.util.CallbackTimberTree
import com.michaelpohl.wifitool.ui.screens.mainscreen.MainScreen
import com.michaelpohl.wifitool.ui.screens.mainscreen.MainScreenViewModel
import com.michaelpohl.wifitool.ui.theme.WifiToolTheme
import timber.log.Timber
import java.io.IOException

class MainActivity : ComponentActivity() {

    private val serviceConnection = MonitoringServiceConnection(this::class.java).apply {
        onServiceConnectedListener = {
            Timber.d("Connected!")
            it.getService().wifiStateListener = { state -> onMonitoringStateChanged(state)}
        }
    }

    private lateinit var viewModel: MainScreenViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initTimber()

        setContent {
            WifiToolTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    viewModel = viewModel()
                    MainScreen(viewModel)
                }
            }
        }
    }

    private fun onMonitoringStateChanged(state : MonitoringLooper.State) {
        viewModel.onMonitoringStateChanged(state)
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
