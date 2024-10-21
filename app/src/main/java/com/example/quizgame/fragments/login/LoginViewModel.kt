package com.example.quizgame.fragments.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.quizgame.constants.FireStoreConstants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    fun handleLoginValidation(email: String, password: String) {
        if (email.isBlank() && password.isBlank()) {
            _message.value = FireStoreConstants.KEY_FILL_ALL_FIELDS
        } else {
            loginUser(email, password)
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                if (user != null) {
                    firestore.collection(FireStoreConstants.KEY_USERS).document(user.uid).get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                _message.value = FireStoreConstants.KEY_LOGIN_SUCCESS
                            }
                        }.addOnFailureListener {
                            _message.value = FireStoreConstants.KEY_LOGIN_FAILED
                        }
                }
            } else {
                _message.value = FireStoreConstants.KEY_LOGIN_FAILED
            }
        }
    }

    fun alreadyLoggedIn(): Boolean {
        return auth.currentUser != null
    }
}
