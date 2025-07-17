package com.example.lifetrack.ui.state

sealed class  UIState {
    object Idle : UIState() // Default idle state
    object Loading : UIState() // Represents a loading state
    object Success : UIState() // Success state
    data class Error(val message: String) : UIState() // Error with a message
}