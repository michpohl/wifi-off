package com.michaelpohl.wifitool.ui.screens.mainscreen

import com.michaelpohl.wifitool.ui.common.UIStateFlowViewModel

class MainScreenViewModel : UIStateFlowViewModel<MainScreenState>() {

    override fun initUIState(): MainScreenState {
        return MainScreenState("test")
    }
}
