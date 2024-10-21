package com.example.quizgame.fragments.quiz

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.quizgame.authentication.UserModelClass
import com.example.quizgame.constants.FireStoreConstants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class QuizViewModel : ViewModel() {
    private val _spinChances = MutableLiveData<Long>()

    private val _username = MutableLiveData<String?>()
    val username: LiveData<String?> = _username

    private val _coins = MutableLiveData<Long>()
    val coins: LiveData<Long> = _coins

    private val _questions = MutableLiveData<List<QuizModelClass>>()
    val questions: LiveData<List<QuizModelClass>> = _questions

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    fun fetchUsername() {
        FirebaseAuth.getInstance().currentUser?.let { user ->
            Firebase.firestore.collection(FireStoreConstants.KEY_USERS).document(user.uid).get()
                .addOnSuccessListener { document ->
                    val userModel = document.toObject(UserModelClass::class.java)
                    _username.value = userModel?.username
                }
                .addOnFailureListener {
                    _message.value = FireStoreConstants.KEY_FAILED_TO_FETCH_USER_DATA
                }
        }
    }

    fun fetchCoins() {
        Firebase.auth.currentUser?.let { user ->
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

    fun fetchSpinChances() {
        Firebase.auth.currentUser?.let { user ->
            Firebase.database.reference.child(FireStoreConstants.KEY_SPIN_CHANCES).child(user.uid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        _spinChances.value = snapshot.value as? Long ?: 0
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _message.value = FireStoreConstants.KEY_FAILED_TO_FETCH_SPIN_CHANCES
                    }
                })
        }
    }

    fun fetchQuestions(subject: String?) {
        if (subject == null) return
        Firebase.firestore.collection(FireStoreConstants.KEY_ALL_QUESTIONS).document(subject)
            .collection(FireStoreConstants.KEY_QUESTION1).get()
            .addOnSuccessListener { questionData ->
                val questionsList = mutableListOf<QuizModelClass>()
                for (data in questionData.documents) {
                    val question = data.toObject(QuizModelClass::class.java)
                    question?.let {
                        questionsList.add(it)
                    }
                }
                _questions.value = questionsList
            }.addOnFailureListener {
                _message.value = FireStoreConstants.KEY_FAILED_TO_FETCH_QUESTIONS
            }
    }

    fun updateSpinChances() {
        Firebase.auth.currentUser?.let { user ->
            Firebase.database.reference.child(FireStoreConstants.KEY_SPIN_CHANCES).child(user.uid)
                .get().addOnSuccessListener { snapshot ->
                    val currentChances = snapshot.value as? Long ?: 0
                    Firebase.database.reference.child(FireStoreConstants.KEY_SPIN_CHANCES).child(user.uid)
                        .setValue(currentChances + 1)
                        .addOnSuccessListener {
                            _spinChances.value = currentChances + 1
                        }
                        .addOnFailureListener {
                            _message.value = FireStoreConstants.KEY_FAILED_TO_UPDATE_SPIN_CHANCES
                        }
                }.addOnFailureListener {
                    _message.value = FireStoreConstants.KEY_FAILED_TO_FETCH_SPIN_CHANCES
                }
        }
    }
}
