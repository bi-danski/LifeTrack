package com.example.lifetrack.presenter

import com.example.lifetrack.model.data.AuthResult
//import com.example.lifetrack.model.network.KtorClientFactory
import com.example.lifetrack.model.repository.AuthRepository
import com.example.lifetrack.view.AuthView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.cancel

class AuthPresenter(
    var view: AuthView?,
    private val repository: AuthRepository,
//    private val httpClient: KtorClientFactory = KtorClientFactory(),
    private val scope: CoroutineScope
) {

    fun login(email: String, password: String) {
        view?.showLoading(true, "Logging in...")
        scope.launch {
            val result = repository.login(email, password)
            handleAuthResult(result)
        }
    }

    fun signUp(email: String, password: String, phoneNumber: String, displayName: String) {
        view?.showLoading(true, "Registering...")
        scope.launch {
            val result = withContext(Dispatchers.IO) {
                repository.signUp(email, password, phoneNumber, displayName)
            }
            handleAuthResult(result)
        }
    }

    private fun handleAuthResult(result: AuthResult) {
        view?.showLoading(false)

        when (result) {
            is AuthResult.Success -> {
                view?.onAuthSuccess()
            }
            is AuthResult.Failure -> {
                view?.showError(result.message)
            }
            is AuthResult.Loading -> {
                view?.showLoading(true, "Please wait...")
            }

            is AuthResult.SuccessWithData<*> -> {
                view?.onAuthSuccess()
            }
        }
    }

    fun detachView() {
        view = null
        scope.coroutineContext.cancel()
    }
}