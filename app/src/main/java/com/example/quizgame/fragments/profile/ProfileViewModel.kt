package com.example.quizgame.fragments.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.quizgame.authentication.UserModelClass
import com.example.quizgame.constants.FireStoreConstants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileViewModel : ViewModel() {
    private val _userData = MutableLiveData<UserModelClass?>()
    val userData: LiveData<UserModelClass?> = _userData

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?>  = _message

    fun fetchUserData() {
        FirebaseAuth.getInstance().currentUser?.let { user ->
            Firebase.firestore.collection(FireStoreConstants.KEY_USERS).document(user.uid)
                .addSnapshotListener { documentSnapshot, error ->
                    if (error != null) {
                        _message.value = FireStoreConstants.KEY_FAILED_TO_FETCH_USER_DATA
                        return@addSnapshotListener
                    }
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        val userModel = documentSnapshot.toObject(UserModelClass::class.java)
                        _userData.value = userModel
                    } else {
                        _message.value = FireStoreConstants.KEY_FAILED_TO_FETCH_USER_DATA
                    }
                }
        }
    }
}
