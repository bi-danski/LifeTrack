package com.example.lifetrack.view

import com.example.lifetrack.model.data.User

interface UserView{
    fun showUserData(user: User)
    fun updateUserUI(user: User)
    fun showMessage(message: String)
    fun showError(message: String)
    fun onLogout()
}