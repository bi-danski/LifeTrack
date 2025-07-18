package com.example.lifetrack.presenter

import com.example.lifetrack.model.data.AuthResult
import com.example.lifetrack.model.data.Practitioner
import com.example.lifetrack.model.data.User
import com.example.lifetrack.model.data.Kiongozi
import com.example.lifetrack.view.UserView
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.lifetrack.model.repository.UserRepositoryImpl

class UserPresenter(
    private val view: UserView,
    private val userRepository: UserRepositoryImpl
) {
    private var userListener: ListenerRegistration? = null

    fun loadCurrentUser() {
        CoroutineScope(Dispatchers.Main).launch {
            val user = userRepository.getCurrentUser()
            if (user != null) {
                view.showUserData(user)
            } else {
                view.showError("User not found")
            }
        }
    }

    fun startUserObserver(userId: String) {
        userListener = userRepository.observeUser(userId) { user: User? ->
            user?.let {
                view.updateUserUI(it)
            }
        }
    }

    fun stopUserObserver() {
        userListener?.remove()
    }

    fun updateUser(userId: String, user: User) {
        CoroutineScope(Dispatchers.Main).launch {
            when (val result = userRepository.updateUser(userId, user)) {
                is AuthResult.Success -> view.showMessage("Profile updated")
                is AuthResult.Failure -> view.showError(result.message)
                AuthResult.Loading -> view.showMessage("Updating profile...")
                is AuthResult.SuccessWithData<*> -> {}
            }
        }
    }

    fun logout() {
        CoroutineScope(Dispatchers.Main).launch {
            userRepository.logout()
            view.onLogout()
        }
    }

    fun sendPasswordReset(email: String) {
        CoroutineScope(Dispatchers.Main).launch {
            when (val result = userRepository.sendPasswordReset(email)) {
                is AuthResult.Success -> view.showMessage("Reset email sent")
                is AuthResult.Failure -> view.showError(result.message)
                AuthResult.Loading -> view.showMessage("Sending password reset email...")
                is AuthResult.SuccessWithData<*> -> {}
            }
        }
    }

    fun getPractitioners(onResult: (List<Practitioner>) -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            val practitioners = userRepository.getPractitioners()
            onResult(practitioners)
        }
    }

    fun addPractitioner(practitioner: Practitioner, onResult: (Practitioner) -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            val result = userRepository.addPractitioner(practitioner)
            when (result) {
                is AuthResult.SuccessWithData<*> -> onResult(result.data as Practitioner)
                is AuthResult.Failure -> view.showError(result.message)
                AuthResult.Loading -> {}
                is AuthResult.Success -> {} // No data to return
            }
        }
    }

    fun updatePractitioner(practitioner: Practitioner, onResult: (Practitioner) -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            val result = userRepository.updatePractitioner(practitioner)
            when (result) {
                is AuthResult.SuccessWithData<*> -> onResult(result.data as Practitioner)
                is AuthResult.Failure -> view.showError(result.message)
                AuthResult.Loading -> {}
                is AuthResult.Success -> {}
            }
        }
    }
    fun deletePractitioner(practitioner: Practitioner, onResult: () -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            val result = userRepository.deletePractitioner(practitioner)
            when (result) {
                is AuthResult.Success -> onResult()
                is AuthResult.Failure -> view.showError(result.message)
                AuthResult.Loading -> {}
                is AuthResult.SuccessWithData<*> -> {}
            }
        }
    }

    fun getAdmins(onResult: (List<Kiongozi>) -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            val admins = userRepository.getAdmins()
            onResult(admins)
        }
    }

    fun addAdmin(admin: Kiongozi, onResult: (Kiongozi) -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            val result = userRepository.addAdmin(admin)
            when (result) {
                is AuthResult.SuccessWithData<*> -> onResult(result.data as Kiongozi)
                is AuthResult.Failure -> view.showError(result.message)
                AuthResult.Loading -> {}
                is AuthResult.Success -> {}
            }
        }
    }

    fun updateAdmin(admin: Kiongozi, onResult: (Kiongozi) -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            val result = userRepository.updateAdmin(admin)
            when (result) {
                is AuthResult.SuccessWithData<*> -> onResult(result.data as Kiongozi)
                is AuthResult.Failure -> view.showError(result.message)
                AuthResult.Loading -> {}
                is AuthResult.Success -> {}
            }
        }
    }

    fun deleteAdmin(admin: Kiongozi, onResult: () -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            val result = userRepository.deleteAdmin(admin)
            when (result) {
                is AuthResult.Success -> onResult()
                is AuthResult.Failure -> view.showError(result.message)
                AuthResult.Loading -> {}
                is AuthResult.SuccessWithData<*> -> {}
            }
        }
    }

    fun getPatients(onResult: (List<User>) -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            val patients = userRepository.getPatients()
            onResult(patients)
        }
    }

    fun addPatient(patient: User, onResult: (User) -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            val result = userRepository.addPatient(patient)
            when (result) {
                is AuthResult.SuccessWithData<*> -> onResult(result.data as User)
                is AuthResult.Failure -> view.showError(result.message)
                AuthResult.Loading -> {}
                is AuthResult.Success -> {}
            }
        }
    }

    fun updatePatient(patient: User, onResult: (User) -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            val result = userRepository.updatePatient(patient)
            when (result) {
                is AuthResult.SuccessWithData<*> -> onResult(result.data as User)
                is AuthResult.Failure -> view.showError(result.message)
                AuthResult.Loading -> {}
                is AuthResult.Success -> {}
            }
        }
    }

    fun deletePatient(patient: User, onResult: () -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            val result = userRepository.deletePatient(patient)
            when (result) {
                is AuthResult.Success -> onResult()
                is AuthResult.Failure -> view.showError(result.message)
                AuthResult.Loading -> {}
                is AuthResult.SuccessWithData<*> -> {}
            }
        }
    }
}