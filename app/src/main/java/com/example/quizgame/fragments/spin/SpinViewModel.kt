package com.example.quizgame.fragments.spin

import android.os.CountDownTimer
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
import kotlin.math.sin
import kotlin.random.Random

class SpinViewModel : ViewModel() {
    private val _username = MutableLiveData<String?>()
    val username: LiveData<String?> = _username

    private val _coins = MutableLiveData<Long>()
    val coins: LiveData<Long> = _coins

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    private val _spinChances = MutableLiveData<Long>()
    val spinChances: LiveData<Long> = _spinChances

    private val _spinResult = MutableLiveData<String>()
    val spinResult: LiveData<String> = _spinResult

    private val _rotation = MutableLiveData<Float>()
    val rotation: LiveData<Float> = _rotation

    private lateinit var timer: CountDownTimer
    private var currentCoins = 0L
    private val itemTitles = arrayOf("100", "Try Again", "1000", "Try Again", "500", "Try Again")

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
                        val coins = snapshot.getValue(Long::class.java) ?: 0
                        _coins.value = coins
                        currentCoins = coins
                    }
                    override fun onCancelled(error: DatabaseError) {
                        _message.value = FireStoreConstants.KEY_FAILED_TO_FETCH_COIN_DATA
                    }
                })
        }
    }

    fun fetchSpinChances() {
        FirebaseAuth.getInstance().currentUser?.let { user ->
            Firebase.database.reference.child(FireStoreConstants.KEY_SPIN_CHANCES).child(user.uid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val chances = snapshot.getValue(Long::class.java) ?: 0
                        _spinChances.value = chances
                    }
                    override fun onCancelled(error: DatabaseError) {
                        _message.value = FireStoreConstants.KEY_FAILED_TO_FETCH_SPIN_CHANCES
                    }
                })
        }
    }

    fun showResult(result: String) {
        _spinResult.value = result
        FirebaseAuth.getInstance().currentUser?.let { user ->
            val currentChances = _spinChances.value ?: 0
            if (currentChances > 0) {
                Firebase.database.reference.child(FireStoreConstants.KEY_SPIN_CHANCES).child(user.uid)
                    .setValue(currentChances - 1)
            }
            when (result) {
                FireStoreConstants.HUNDRED,
                FireStoreConstants.FIVE_HUNDRED,
                FireStoreConstants.THOUSAND -> {
                    val wonCoins = result.toLong()
                    val updatedCoins = currentCoins + wonCoins
                    Firebase.database.reference.child(FireStoreConstants.KEY_COINS).child(user.uid)
                        .setValue(updatedCoins)
                        .addOnSuccessListener {
                            _coins.value = updatedCoins
                        }
                        .addOnFailureListener {
                            _message.value = FireStoreConstants.KEY_FAILED_TO_UPDATE_COIN
                        }
                }
                else -> {}
            }
        }
    }

    fun startSpin() {
        val spin = Random.nextInt(6)
        val fullRotations = 8 * 360
        val degree = fullRotations + (spin * (360 / itemTitles.size))
        val slowDownRotation = 3 * 360

        timer = object : CountDownTimer(10000, 15) {
            var rotation = 0f
            val totalTicks = 10000 / 15
            var currentTick = 0
            override fun onTick(finished: Long) {
                currentTick++
                val t = currentTick / totalTicks.toFloat()
                val easedSpeed = (sin(t * Math.PI - Math.PI / 2) + 1) / 2 * 40
                if (rotation >= degree - slowDownRotation) {
                    val slowdownFactor = (degree - rotation) / slowDownRotation
                    rotation += (easedSpeed * slowdownFactor).toFloat()
                } else {
                    rotation += easedSpeed.toFloat()
                }
                _rotation.value = rotation

                if (rotation >= degree) {
                    rotation = degree.toFloat()
                    timer.cancel()
                    showResult(itemTitles[spin])
                }
            }

            override fun onFinish() {
                _rotation.value = degree.toFloat()
                showResult(itemTitles[spin])
            }
        }.start()
    }
}








