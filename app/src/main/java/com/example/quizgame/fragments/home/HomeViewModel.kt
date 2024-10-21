package com.example.quizgame.fragments.home
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.quizgame.authentication.UserModelClass
import com.example.quizgame.constants.FireStoreConstants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeViewModel : ViewModel() {
    private val _username = MutableLiveData<String?>()
    val username: LiveData<String?> = _username

    private val _coins = MutableLiveData<Long>()
    val coins: LiveData<Long> = _coins

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    private val _checkList = MutableLiveData<List<CategoryModelClass?>>()
    val checkList: LiveData<List<CategoryModelClass?>> = _checkList


    fun setRecyclerForCategoryList() {
        _checkList.value = SubjectName.categoryList
    }


    fun fetchUsername() {
        FirebaseAuth.getInstance().currentUser?.let { user ->
            Firebase.firestore.collection(FireStoreConstants.KEY_USERS).document(user.uid)
                .addSnapshotListener { documentSnapshot, error ->
                    if (error != null) {
                        _message.value = FireStoreConstants.KEY_FAILED_TO_FETCH_USER_DATA
                        return@addSnapshotListener
                    }
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        val userModel = documentSnapshot.toObject(UserModelClass::class.java)
                        _username.value = userModel?.username
                    } else {
                        _message.value = FireStoreConstants.KEY_FAILED_TO_FETCH_USER_DATA
                    }
                }
        }
    }

    fun fetchCoins() {
        FirebaseAuth.getInstance().currentUser?.let { user ->
            Firebase.database.reference.child(FireStoreConstants.KEY_COINS).child(user.uid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        _coins.value = snapshot.value as? Long ?: 0
                    }
                    override fun onCancelled(error: DatabaseError) {
                        _message.value = FireStoreConstants.KEY_FAILED_TO_FETCH_COIN_DATA
                    }
                })
        }
    }
}
