package com.michaelpohl.wifitool.ui.common

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * A base ViewModel class with some helper functionality around the pattern using
 * UI state objects (it's pretty much the same as MVI/BLoC)
 */
abstract class UIStateFlowViewModel<T : Any> : ViewModel() {

    private val _stateFlow = MutableStateFlow(this.initUIState())
    val stateFlow: StateFlow<T> = _stateFlow

    /**
     * Convenience field to easily access the flow's current value
     */
    protected val currentState: T
        get() {
            return stateFlow.value
        }

    /**
     * Implement this method to define what the initial UI state should look like
     */
    abstract fun initUIState(): T

    /**
     * Function to push the next state to the flow in
     */
    protected fun updateState(newValue: T) {
        _stateFlow.value = newValue
    }
}
