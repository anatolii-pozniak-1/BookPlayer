package com.test.bookplayer.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseViewModel<State>(
    initialUiState: State,
) : ViewModel() {
    private val _uiState: MutableStateFlow<State> = MutableStateFlow(initialUiState)
    val uiState = _uiState.asStateFlow()

    /**
     * Set new Ui State
     */
    protected fun setState(reduce: State.() -> State) {
        val newState = _uiState.value.reduce()
        _uiState.value = newState
    }
}