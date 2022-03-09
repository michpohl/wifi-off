package com.michaelpohl.wifitool

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import com.michaelpohl.design.theme.WifiTheme
import com.michaelpohl.wifiservice.MonitoringService
import com.michaelpohl.wifiservice.MonitoringServiceConnection
import com.michaelpohl.wifiservice.looper.MonitoringState
import com.michaelpohl.wifitool.common.util.CallbackTimberTree
import com.michaelpohl.wifitool.ui.screens.mainscreen.MainScreen
import com.michaelpohl.wifitool.ui.screens.mainscreen.MainScreenViewModel
import com.michaelpohl.wifitool.ui.screens.mainscreen.MainScreenViewModelFactory
import com.michaelpohl.wifitool.ui.theme.WifiToolTheme
import timber.log.Timber

class MainActivity : ComponentActivity() {

    private val serviceConnection = MonitoringServiceConnection(this::class.java).apply {
        onServiceConnectedListener = {
            Timber.d("Connected!")
            it.getService().wifiStateListener = { state -> onMonitoringStateChanged(state) }
            viewModel.getSavedWifis()
        }
    }

    private lateinit var viewModel: MainScreenViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WifiTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    viewModel = MainScreenViewModelFactory(serviceConnection).build()
                    Timber.d("ViewModel is initialized")
                    MainScreen(viewModel)
                }
            }
        }
    }

    private fun onMonitoringStateChanged(state: MonitoringState) {
        viewModel.onMonitoringStateChanged(state)
    }

    private fun initTimber() {
        // There have been inconsistencies when reusing Timber from a previous activity instance,
        // so it seems safer to always throw them out on restart.
        Timber.uprootAll()
        Timber.plant(CallbackTimberTree {
            if (::viewModel.isInitialized) viewModel.onTimberMessage(
                it
            )
        })
        Timber.d("Timber is on")
    }

    private fun showToast(it: String) {
        Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        initTimber()
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
