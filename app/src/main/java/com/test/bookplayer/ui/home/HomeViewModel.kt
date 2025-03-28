package com.test.bookplayer.ui.home

import androidx.lifecycle.ViewModel
import com.test.bookplayer.data.BooksDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeScreenState.initial)
    val uiState = _uiState.asStateFlow()

    init {
        loadBooks()
    }

    fun handleEvent(event: HomeScreenEvent) {
    }

    private fun loadBooks() {
        val audioBooks = BooksDataStore.books

        _uiState.value = HomeScreenState(
            index = 0,
            audioBooks = audioBooks,
            keyPoints = audioBooks[0].summary,
        )
    }
}