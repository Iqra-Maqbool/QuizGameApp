package com.example.quizgame.fragments.signup

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.quizgame.authentication.UserModelClass
import com.example.quizgame.constants.FireStoreConstants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    fun handleSignupValidation(
        username: String, email: String, password: String, cPassword: String
    ) {
        if (username.isBlank() && email.isBlank() && password.isBlank() && cPassword.isBlank()) {
            _message.value = FireStoreConstants.KEY_FILL_ALL_FIELDS
        } else {
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                if (password == cPassword) {
                    signupUser(username, email, password)
                }
            }
        }
    }

    private fun signupUser(username: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                user?.let {
                    val userModelClass = UserModelClass(username, email, password)
                    firestore.collection(FireStoreConstants.KEY_USERS).document(user.uid)
                        .set(userModelClass).addOnCompleteListener { firestoreTask ->
                            if (firestoreTask.isSuccessful) {
                                _message.value = FireStoreConstants.KEY_SIGNUP_SUCCESS
                            } else {
                                _message.value = FireStoreConstants.KEY_SIGNUP_FAILED
                            }
                        }
                }
            } else {
                _message.value = FireStoreConstants.KEY_SIGNUP_FAILED
            }
        }
    }
}
